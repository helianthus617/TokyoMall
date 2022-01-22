package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    //    本地缓存
//    private Map<String, Object> cache = new HashMap<>();
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(new Query<CategoryEntity>().getPage(params), new QueryWrapper<CategoryEntity>());
        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> entities = baseMapper.selectList(null);
        List<CategoryEntity> leve1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return leve1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    //    @CacheEvict(value = "category",key = "'getLevel1Categorys'") 只能删除一个
//    @CacheEvict(value = "category",key = "'getLevel1Categorys'")
//  级联清空缓存方式一
//    @Caching(evict = {
//            @CacheEvict(value = "category",key = "'getLevel1Categorys'"),
//            @CacheEvict(value = "category",key = "'getCatalogJson'")
//    })
//  级联清空缓存方式二
//  @CachePut 双写模式，该方法不支持没有返回值
    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    //每一个需要缓存的数据我们都指定要放到那个名字的缓存[缓存的分区(建议按照业务类型)]{"category"}
    //代表当前方法的结果需要缓存 如果缓存中有 方法不调用,如果缓存中没有,会调用方法,最后将方法的结果放入缓存中
//默认行为:
//         如果缓存中有不调用方法
//         key默认自动生成,缓存的名字::SimpleKey[] 自主生成的key值 category::SimpleKey []
//         缓存的value值 默认使用jdk序列化 将序列化后的数据方法redis中
//         默认时间TTL时间是-1 是永久的
//         自定义key   key属性指定 接受一个spel表达式  字符串 key="'Level1Categorys'
//         自定义时间   配置文件中修改 TTL 时间 spring.cache.redis.time-to-live=3600000
//         自定义json 格式存储
//         sync = true 启动本地锁
    @Cacheable(value = {"category"}, key = "#root.method.name", sync = true)
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }


//    最初未优化版本 版本1
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//        List<CategoryEntity> level1Categorys = getLevel1Categorys();
//        // 2 封装数据
//        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//                    // 1 每一个的一级分类，查到这个一级分类的二级分类
//                    List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
//                    // 2 分装上面的结果
//                    List<Catelog2Vo> catelog2Vos = null;
//
//                    if (categoryEntities != null) {
//
//                        catelog2Vos = categoryEntities.stream().map(l2 -> {
//                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                            // 1 找当前二级分类的三级分类封装成vo
//                            List<CategoryEntity> level3Catelog = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",l2.getCatId()) );
//                            if (level3Catelog != null) {
//                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
//                                    // 2 分装成指定格式
//                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                                    return catelog3Vo;
//                                }).collect(Collectors.toList());
//                                catelog2Vo.setCatalog3List(collect);
//                            }
//                            return catelog2Vo;
//                        }).collect(Collectors.toList());
//                    }
//                    return catelog2Vos;
//                }
//        ));
//        return parent_cid;
//    }


//        将数据库的多次查询变为一次,版本二
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
//
//        List<CategoryEntity> selectList = baseMapper.selectList(null);
//
//        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
//        // 2 封装数据
//        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//                    // 1 每一个的一级分类，查到这个一级分类的二级分类
//                    List<CategoryEntity> categoryEntities = getParent_cid(selectList,v.getCatId());
//                    // 2 分装上面的结果
//                    List<Catelog2Vo> catelog2Vos = null;
//                    if (categoryEntities != null) {
//                        catelog2Vos = categoryEntities.stream().map(l2 -> {
//                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                            // 1 找当前二级分类的三级分类封装成vo
//                            List<CategoryEntity> level3Catelog = getParent_cid(selectList,l2.getCatId());
//                            if (level3Catelog != null) {
//                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
//                                    // 2 分装成指定格式
//                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                                    return catelog3Vo;
//                                }).collect(Collectors.toList());
//                                catelog2Vo.setCatalog3List(collect);
//                            }
//                            return catelog2Vo;
//                        }).collect(Collectors.toList());
//                    }
//                    return catelog2Vos;
//                }
//        ));
//        return parent_cid;
//    }
//
//   private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parent_cid) {
//   List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
//   return collect;
//   }


    //       本地缓存 版本三
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
////        如果缓存中有就是用缓存
//        Map<String, List<Catelog2Vo>> catalogJson = (Map<String, List<Catelog2Vo>>) cache.get("catalogJson");
//
//        if (cache.get("catalogJson") == null) {
//            List<CategoryEntity> selectList = baseMapper.selectList(null);
//            List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
//            // 2 封装数据
//            Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//                        // 1 每一个的一级分类，查到这个一级分类的二级分类
//                        List<CategoryEntity> categoryEntities = getParent_cid(selectList,v.getCatId());
//                        // 2 分装上面的结果
//                        List<Catelog2Vo> catelog2Vos = null;
//                        if (categoryEntities != null) {
//                            catelog2Vos = categoryEntities.stream().map(l2 -> {
//                                Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                                // 1 找当前二级分类的三级分类封装成vo
//                                List<CategoryEntity> level3Catelog = getParent_cid(selectList,l2.getCatId());
//                                if (level3Catelog != null) {
//                                    List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
//                                        // 2 分装成指定格式
//                                        Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                                        return catelog3Vo;
//                                    }).collect(Collectors.toList());
//                                    catelog2Vo.setCatalog3List(collect);
//                                }
//                                return catelog2Vo;
//                            }).collect(Collectors.toList());
//                        }
//                        return catelog2Vos;
//                    } ));
//            cache.put("catalogJson",parent_cid);
//            return parent_cid;
//        }
//        return catalogJson;
//    }


    //方式四 使用分布式缓存，未加锁
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    // 1 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                    // 2 分装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                            // 1 找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                    // 2 分装成指定格式
                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }
        ));
        return parent_cid;
    }


//    //方式三 分布式缓存
//    @Override
//    public Map<String, List<Catelog2Vo>> getCatalogJson() {
////        1.空结果缓存
////        2.设置过期时间
////        3.加锁解决缓存击穿
//        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
//        if (StringUtils.isEmpty(catalogJSON)) {
//            System.out.println("缓存不命中");
//            Map<String, List<Catelog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
////不能将放入的缓存的操作 放到锁外面,因为与redis 交互放缓存需要时间,在未完成放入缓存的时间内,其他线程又会去查数据库
////            String s = JSON.toJSONString(catalogJsonFromDb);
////            redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
//        }
//        System.out.println("缓存命中");
//        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//        });
//        return result;
//    }

    //使用redisson处理分布式锁
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
        //1. 锁的名字;
        RLock lock = redissonClient.getLock("CatalogJson-lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;
    }


    //    从数据查询并封装分类数据(redis分布式锁,版本四，设置UUID ，获取值对比，删除自己的锁
//    问题:解决任务续期。简单办法就是设置 释放锁时间加长
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//        String uuid = UUID.randomUUID().toString();
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
//        if (lock) {
//            System.out.println("获取分布式锁成功");
//            Map<String, List<Catelog2Vo>> dataFromDb;
//            try {
//                dataFromDb = getDataFromDb();
//            } finally {
//                //执行业务
//                //获取值对比+对比成功删除=原子操作 lua 脚本解锁
//                String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
//                        "then\n" +
//                        "    return redis.call(\"del\",KEYS[1])\n" +
//                        "else\n" +
//                        "    return 0\n" +
//                        "end";
//                redisTemplate.execute(new DefaultRedisScript<Integer>(script, Integer.class), Arrays.asList("lock"), uuid);
//            }
//            return dataFromDb;
//        } else {
//            System.out.println("获取分布式锁失败，稍后重试");
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return getCatalogJsonFromDb();
//        }
//    }

//    从数据查询并封装分类数据(redis分布式锁,版本三，设置过期时间和加锁操作必须是原子性的
//    存在问题:如果业务时间很长，锁自己过期了，我们直接删除，有可能把别人持有的锁给删掉
//    解决: 占锁的时候值指定uuid 每个人匹配自己的锁才删除)
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1111",300,TimeUnit.SECONDS);
//        if (lock) {
//            //执行业务
//            //突然断电
//            Map<String, List<Catelog2Vo>> dataFromDb = getDataFromDb();
//            redisTemplate.delete("lock");
//            return dataFromDb;
//        } else {
//            return getCatalogJsonFromDb();
//        }
//
//    }


    //从数据查询并封装分类数据(redis分布式锁,版本二，出现问题:存在锁未成功设置过期时间的情况。)
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1111");
//        if (lock) {
//            //突然断电
//            redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
//            Map<String, List<Catelog2Vo>> dataFromDb = getDataFromDb();
//            redisTemplate.delete("lock");
//            return dataFromDb;
//        } else {
//            return getCatalogJsonFromDb();
//        }
//
//    }


    //从数据查询并封装分类数据(redis分布式锁,版本一，出现问题:存在锁不能删除的情况。)
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1111");
//        if (lock) {
//            Map<String, List<Catelog2Vo>> dataFromDb = getDataFromDb();
//            redisTemplate.delete("lock");
//            return dataFromDb;
//        } else {
//            return getCatalogJsonFromDb();
//        }
//
//    }

    private Map<String, List<Catelog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
            return result;
        }
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2 封装数据
        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    // 1 每一个的一级分类，查到这个一级分类的二级分类
                    List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
                    // 2 分装上面的结果
                    List<Catelog2Vo> catelog2Vos = null;
                    if (categoryEntities != null) {
                        catelog2Vos = categoryEntities.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                            // 1 找当前二级分类的三级分类封装成vo
                            List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                            if (level3Catelog != null) {
                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
                                    // 2 分装成指定格式
                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                                    return catelog3Vo;
                                }).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(collect);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());
                    }
                    return catelog2Vos;
                }
        ));
        //将放入缓存的操作放入锁内
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    //从数据查询并封装分类数据(本地锁)
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
////        只要是同一把锁,就能锁住需要这个锁的所有线程
////        synchronized (this) SpringBoot所有的组件在容器中都是单例的
////        本地锁  synchronized JUC ,在分布式下想要锁住所有必须使用分布式锁
//        synchronized (this) {
//            String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
//            if (!StringUtils.isEmpty(catalogJSON)) {
//                Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {
//                });
//                return result;
//            }
//
//            List<CategoryEntity> selectList = baseMapper.selectList(null);
//            List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
//            // 2 封装数据
//            Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//                        // 1 每一个的一级分类，查到这个一级分类的二级分类
//                        List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());
//                        // 2 分装上面的结果
//                        List<Catelog2Vo> catelog2Vos = null;
//                        if (categoryEntities != null) {
//                            catelog2Vos = categoryEntities.stream().map(l2 -> {
//                                Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                                // 1 找当前二级分类的三级分类封装成vo
//                                List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
//                                if (level3Catelog != null) {
//                                    List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
//                                        // 2 分装成指定格式
//                                        Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                                        return catelog3Vo;
//                                    }).collect(Collectors.toList());
//                                    catelog2Vo.setCatalog3List(collect);
//                                }
//                                return catelog2Vo;
//                            }).collect(Collectors.toList());
//                        }
//                        return catelog2Vos;
//                    }
//            ));
//            //将放入缓存的操作放入锁内
//            String s = JSON.toJSONString(parent_cid);
//            redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
//            return parent_cid;
//        }
//    }


//从数据查询并封装分类数据(未加锁)
//    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDb() {
//
//        List<CategoryEntity> selectList = baseMapper.selectList(null);
//
//        List<CategoryEntity> level1Categorys = getParent_cid(selectList,0L);
//        // 2 封装数据
//        Map<String, List<Catelog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
//                    // 1 每一个的一级分类，查到这个一级分类的二级分类
//                    List<CategoryEntity> categoryEntities = getParent_cid(selectList,v.getCatId());
//                    // 2 分装上面的结果
//                    List<Catelog2Vo> catelog2Vos = null;
//                    if (categoryEntities != null) {
//                        catelog2Vos = categoryEntities.stream().map(l2 -> {
//                            Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
//                            // 1 找当前二级分类的三级分类封装成vo
//                            List<CategoryEntity> level3Catelog = getParent_cid(selectList,l2.getCatId());
//                            if (level3Catelog != null) {
//                                List<Catelog2Vo.Catelog3Vo> collect = level3Catelog.stream().map(l3 -> {
//                                    // 2 分装成指定格式
//                                    Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
//                                    return catelog3Vo;
//                                }).collect(Collectors.toList());
//                                catelog2Vo.setCatalog3List(collect);
//                            }
//                            return catelog2Vo;
//                        }).collect(Collectors.toList());
//                    }
//                    return catelog2Vos;
//                }
//        ));
//        return parent_cid;
//    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}