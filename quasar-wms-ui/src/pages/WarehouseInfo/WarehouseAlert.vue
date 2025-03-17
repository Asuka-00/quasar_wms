<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-select v-model="search.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions" option-label="warehouseName" option-value="id"
                emit-value map-options @update:model-value="freshData" clearable/>
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.productId" label="产品名称" dense outlined :options="productOptions" option-label="productName" option-value="id"
                emit-value map-options @update:model-value="freshData" clearable/>
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addWarehouseAlert" />
            </div>
        </div>
        <q-table title="仓库预警管理" :rows="rows" :columns="columns" row-key="id" :pagination="pagination" class="q-mt-md">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editWarehouseAlert(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteWarehouseAlert(props.row)">
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
                    <div class="text-h6">{{ addWarehouseAlertDialog ? '新增预警' : '编辑预警' }}</div>
                    <q-form ref="formRef">
                        <q-select v-model="formData.warehouseId" label="仓库名称" dense outlined :options="warehouseOptions" option-label="warehouseName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '仓库名称不能为空']" @update:model-value="() => { formData.productId = null; freshDialogProductOptions(); }"/>
                        <q-select v-model="formData.categoryId" label="商品分类" dense outlined :options="categoryOptions" option-label="categoryName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '商品分类不能为空']" @update:model-value="() => { formData.productId = null; freshDialogProductOptions(); }"/>
                        <q-select v-model="formData.productId" label="产品名称" dense outlined :options="dialogProductOptions" option-label="productName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '产品名称不能为空']"/>
                        <q-input v-model="formData.minStock" label="最小库存" dense outlined type="number"
                            :rules="[
                                (val) => val !== null || '最小库存不能为空',
                                (val) => !isNaN(val) || '请输入数字',
                                (val) => val >= 0 || '最小库存不能小于0'
                            ]"/>
                        <q-input v-model="formData.maxStock" label="最大库存" dense outlined type="number"
                            :rules="[
                                (val) => val !== null || '最大库存不能为空',
                                (val) => !isNaN(val) || '请输入数字',
                                (val) => val >= 0 || '最大库存不能小于0',
                                (val) => formData.minStock === null || val > formData.minStock || '最大库存必须大于最小库存'
                            ]"/>
                        <!-- <q-select v-model="formData.sendType" label="预警方式" dense outlined :options="sendTypeOptions" option-label="sendTypeName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '预警方式不能为空']"/> -->
                        <q-select v-model="formData.sendTo" label="预警对象" dense outlined :options="sendToOptions" option-label="nickName" option-value="id"
                        emit-value map-options clearable :rules="[(val) => val !== null || '预警对象不能为空']"/>
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
    { name: 'warehouseName', label: '仓库名称', field: 'warehouseName', align: "left" },
    { name: 'categoryName', label: '商品分类', field: 'categoryName', align: "left" },
    { name: 'productName', label: '产品名称', field: 'productName', align: "left" },
    { name: 'minStock', label: '最小库存', field: 'minStock', align: "left" },
    { name: 'maxStock', label: '最大库存', field: 'maxStock', align: "left" },
    // { name: 'sendType', label: '预警方式', field: 'sendType', align: "left" },
    { name: 'sendToName', label: '预警对象', field: 'sendToName', align: "left" },
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
    warehouseId: null,
    productId: null
});

const freshData = async () => {
    const res = await axios.get('/web/whAlert/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            warehouseId: search.value.warehouseId,
            productId: search.value.productId
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

const warehouseOptions = ref([]);
const productOptions = ref([]);
const dialogProductOptions = ref([])
const categoryOptions = ref([]);
const sendToOptions = ref([]);

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

const freshProductOptions = async () => {
    const res = await axios.get('/web/product/list',{
        params: {
            current: 1,
            size: 10000
        }
    });
    if(res.data.code === 200){
        productOptions.value = res.data.data.records;
    }
}

const freshDialogProductOptions = async () => {
    const res = await axios.get('/web/product/list',{
        params: {
            current: 1,
            size: 10000,
            categoryId: formData.value.categoryId,
            warehouseId: formData.value.warehouseId
        }
    });
    if(res.data.code === 200){
        dialogProductOptions.value = res.data.data.records;
    }
}

const freshSendToOptions = async () => {
    const res = await axios.post('/web/user/list',{},{
        params: {
            current: 1,
            size: 10000
        }
    });
    if(res.data.code === 200){
        sendToOptions.value = res.data.data.records;
    }
}

const dialogVisible = ref(false);
const addWarehouseAlertDialog = ref(false);

interface FormData {
    warehouseId: number | null;
    categoryId: number | null; 
    productId: number | null;
    minStock: number | null;
    maxStock: number | null;
    sendType: number | null;
    sendTo: number | null;
    id?: number | null;
}

const formData = ref<FormData>({
    warehouseId: null,
    categoryId: null,
    productId: null,
    minStock: null,
    maxStock: null,
    sendType: null,
    sendTo: null
});

const formRef = ref<QForm | null>(null);

const addWarehouseAlert = () => {
    dialogVisible.value = true;
    addWarehouseAlertDialog.value = true;
    formData.value = {
        warehouseId: null,
        categoryId: null,
        productId: null,
        minStock: null,
        maxStock: null,
        sendType: null,
        sendTo: null
    }
}

const editWarehouseAlert = (row: any) => {
    dialogVisible.value = true;
    addWarehouseAlertDialog.value = false;
    formData.value = {
        warehouseId: +row.warehouseId,
        categoryId: +row.categoryId,
        productId: +row.productId,
        minStock: row.minStock,
        maxStock: row.maxStock,
        sendType: row.sendType,
        sendTo: row.sendTo,
        id: row.id
    }
}

const deleteWarehouseAlert = (row: any) => {
    $q.dialog({
        title: '删除预警',
        message: '确定要删除该预警吗？',
        cancel: true,
        persistent: true,
    }).onOk(async () => {
        const res = await axios.get('/web/whAlert/delete', {
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

const submitForm = async () => {
    const valid = await formRef.value?.validate();
    if(!valid){
        return;
    }
    const res = await axios.post('/web/whAlert/saveOrUpdate', formData.value);
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

onMounted(() => {
    freshData();
    freshWarehouseOptions();
    freshProductOptions();
    freshCategoryOptions();
    freshSendToOptions();
});

</script>

<style scoped>

</style>
