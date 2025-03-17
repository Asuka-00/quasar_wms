import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';
import { useUserStore } from '@/stores/userStore';

// Define your routes here
const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        redirect: '/home',
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('@/pages/Home.vue'),
        meta: {
            title: '首页',
            icon: 'home',
            roles: ['user', 'admin']
        }
    },
    {
        path: '/web',
        children: [
            {
                path: 'baseinfo',
                name: 'BaseInfo',
                meta: {
                    title: '基础信息管理',
                    icon: 'settings',
                    roles: ['user', 'admin']
                },
                children: [
                    {
                        path: 'unit',
                        name: 'Unit',
                        component: () => import('@/pages/BaseInfo/Unit.vue'),
                        meta: {
                            title: '单位管理',
                            icon: 'business',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'department',
                        name: 'Department',
                        component: () => import('@/pages/BaseInfo/Department.vue'),
                        meta: {
                            title: '部门管理',
                            icon: 'people',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'user',
                        name: 'User',
                        component: () => import('@/pages/BaseInfo/User.vue'),
                        meta: {
                            title: '用户管理',
                            icon: 'person',
                            roles: [ 'admin']
                        }
                    }
                ]
            },
            {
                path: 'warehouseinfo',
                name: 'WareHouseInfo',
                meta: {
                    title: '仓库信息管理',
                    icon: 'archive',
                    roles: ['user', 'admin']
                },
                children:[
                    {
                        path: 'warehouse',
                        name: 'WareHouse',
                        component: () => import('@/pages/WarehouseInfo/Warehouse.vue'),
                        meta: {
                            title: '仓库管理',
                            icon: 'store',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'warehousealert',
                        name: 'WareHouseAlert',
                        component: () => import('@/pages/WarehouseInfo/WarehouseAlert.vue'),
                        meta: {
                            title: '仓库预警管理',
                            icon: 'warning',
                            roles: ['user', 'admin']
                        }
                    }
                ]
            },
            {
                path: 'productinfo',
                name: 'ProductInfo',
                meta: {
                    title: '产品信息管理',
                    icon: 'inventory',
                    roles: ['user', 'admin']
                },
                children: [
                    {
                        path: 'productcategory',
                        name: 'ProductCategory',
                        component: () => import('@/pages/ProductInfo/ProductCategory.vue'),
                        meta: {
                            title: '商品分类管理',
                            icon: 'category',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'product',
                        name: 'Product',
                        component: () => import('@/pages/ProductInfo/Product.vue'),
                        meta: {
                            title: '商品管理',
                            icon: 'inventory',
                            roles: ['user', 'admin']
                        }
                    }
                ]
            },
            {
                path: 'stockinfo',
                name: 'StockInfo',
                meta: {
                    title: '库存信息管理',
                    icon: 'content_paste',
                    roles: ['user', 'admin']
                },
                children: [
                    {
                        path: 'stockdoc',
                        name: 'StockDoc',
                        component: () => import('@/pages/StockInfo/StockDoc.vue'),
                        meta: {
                            title: '出入库单管理',
                            icon: 'content_paste_go',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'inventory',
                        name: 'Inventory',
                        component: () => import('@/pages/StockInfo/Inventory.vue'),
                        meta: {
                            title: '盘点单管理',
                            icon: 'insert_drive_file',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'realstock',
                        name: 'RealStock',
                        component: () => import('@/pages/StockInfo/RealStock.vue'),
                        meta: {
                            title: '实时库存查询',
                            icon: 'search',
                            roles: ['user', 'admin']
                        }
                    },
                    {
                        path: 'stockhistory',
                        name: 'StockHistory',
                        component: () => import('@/pages/StockInfo/StockHistory.vue'),
                        meta: {
                            title: '出入库历史查询',
                            icon: 'history',
                            roles: ['user', 'admin']
                        }
                    }
                ]
            }
        ]
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/pages/login/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/pages/login/Register.vue')
    }
];

// Create the router instance
const router = createRouter({
    history: createWebHistory(),
    routes
});

// 添加路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore();
    const token = userStore.token;

    // 如果是登录或注册页面，直接放行
    if (to.path === '/login' || to.path === '/register') {
        next();
        return;
    }

    // 如果没有token，重定向到登录页
    if (!token) {
        next('/login');
        return;
    }

    // 有token，允许访问
    next();
});

export default router;