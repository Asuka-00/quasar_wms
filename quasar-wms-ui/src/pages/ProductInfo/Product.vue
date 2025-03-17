<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="search.productName" label="商品名称" dense outlined @keyup.enter="freshData" />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.categoryId" label="商品分类" dense outlined :options="categoryOptions" option-label="categoryName" option-value="id"
                emit-value map-options  @update:model-value="freshData" clearable/>
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.warehouseId" label="所属仓库名称" dense outlined :options="warehouseOptions" option-label="warehouseName" option-value="id"
                emit-value map-options  @update:model-value="freshData" clearable/>
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addProduct" />
            </div>
        </div>
        <q-table title="商品管理" :rows="rows" :columns="columns" row-key="id" :pagination="pagination" class="q-mt-md">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editProduct(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteProduct(props.row)">
                        <q-tooltip>删除</q-tooltip>
                    </q-btn>
                </q-td>
            </template>
            <template v-slot:bottom>
                <div class="row col-12 justify-center q-mt-md">
                    <q-pagination v-model="pagination.page"
                        :max="Math.ceil(pagination.rowsNumber / pagination.rowsPerPage)" @update:model-value="freshData"
                        input />
                </div>
            </template>
        </q-table>

        <!-- 新增/编辑弹窗 -->
        <q-dialog v-model="dialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">{{ addProductDialog ? '新增商品' : '编辑商品' }}</div>
                    <q-form ref="formRef">
                        <q-input v-model="formData.productName" label="商品名称" dense outlined :rules="[(val) => val.length > 0 || '商品名称不能为空']" />
                        <q-select v-model="formData.categoryId" label="商品分类" dense outlined :options="categoryOptions" option-label="categoryName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '商品分类不能为空']"/>
                        <q-select v-model="formData.warehouseId" label="所属仓库名称" dense outlined :options="warehouseOptions" option-label="warehouseName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '所属仓库名称不能为空']"/>
                    </q-form>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn label="取消" color="negative" @click="dialogVisible = false" />
                    <q-btn label="提交" color="primary" @click="submitForm" />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar, type QTableColumn, type QForm } from 'quasar';
import axios from '@/axios/index';

const $q = useQuasar();

const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: "left" },
    { name: 'productName', label: '商品名称', field: 'productName', align: "left" },
    { name: 'categoryName', label: '商品分类', field: 'categoryName', align: "left" },
    { name: 'warehouseName', label: '所属仓库名称', field: 'warehouseName', align: "left" },
    { name: 'createdBy', label: '创建人', field: 'createdBy', align: "left" },
    { name: 'createdTime', label: '创建时间', field: 'createdTime', align: "left" },
    { name: 'updatedBy', label: '更新人', field: 'updatedBy', align: "left" },
    { name: 'updatedTime', label: '更新时间', field: 'updatedTime', align: "left" },
    { name: 'action', label: '操作', field: 'action', align: "left" }
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
});

const search = ref({
    productName: '',
    categoryId: null,
    warehouseId: null
});

const freshData = async () => {
    const res = await axios.get('/web/product/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            productName: search.value.productName,
            categoryId: search.value.categoryId,
            warehouseId: search.value.warehouseId
        }
    });
    if(res.data.code === 200){
        rows.value = res.data.data.records.map((item: any, index: number) => ({
            ...item,
            index: (pagination.value.page - 1) * pagination.value.rowsPerPage + index + 1
        }));
        pagination.value.rowsNumber = res.data.data.total;
    }else{
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
}

const categoryOptions = ref([]);
const warehouseOptions = ref([]);
const freshCategoryOptions = async () => {
    const res = await axios.get('/web/product_category/list',{
        params: {
            current: 1,
            size: 10000
        }
    });
    if(res.data.code === 200){
        categoryOptions.value = res.data.data.records;
    }
}

const freshWarehouseOptions = async () => {
    const res = await axios.get('/web/warehouse/list',{
        params: {
            current: 1,
            size: 10000
        }
    });
    if(res.data.code === 200){
        warehouseOptions.value = res.data.data.records;
    }
}

const dialogVisible = ref(false);
const addProductDialog = ref(false);

interface FormData {
    productName: string;
    categoryId: number | null;
    id?: number | null;
    warehouseId: number | null;
}

const formData = ref<FormData>({
    productName: '',
    categoryId: null,
    warehouseId: null
});

const formRef = ref<QForm | null>(null);

const addProduct = () => {
    dialogVisible.value = true;
    addProductDialog.value = true;
    formData.value = {
        productName: '',
        categoryId: null,
        warehouseId: null
    };
}

const editProduct = (row: any) => {
    dialogVisible.value = true;
    addProductDialog.value = false;
    formData.value = {
        productName: row.productName,
        categoryId: +row.categoryId,
        warehouseId: +row.warehouseId,
        id: row.id
    };
}

const submitForm = async () => {
    const valid = await formRef.value?.validate();
    if(!valid){
        return;
    }
    const res = await axios.post('/web/product/saveOrUpdate', formData.value);
    if(res.data.code === 200){
        $q.notify({
            message: res.data.message,
            color: 'positive',
            position: 'top',
            timeout: 2000,
        });
        dialogVisible.value = false;
        freshData();
    }else{
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000,
        });
    }
}

const deleteProduct = (row: any) => {
    $q.dialog({
        title: '删除商品',
        message: '确定要删除该商品吗？',
        cancel: true,
        persistent: true,
    }).onOk(async () => {
        const res = await axios.get('/web/product/delete', {
            params: {
                id: row.id
            }
        });
        if(res.data.code === 200){
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            freshData();
        }else{
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000,
            });
        }
    })
}
onMounted(() => {
    freshData();
    freshCategoryOptions();
    freshWarehouseOptions();
});
</script>

<style scoped>

</style>
