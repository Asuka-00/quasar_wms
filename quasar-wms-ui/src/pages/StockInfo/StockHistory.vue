<template>
    <div class="q-pa-md">
        <q-tabs v-model="tab" class="text-grey-8" align="left" indicator-color="primary"
            active-color="primary" dense>
            <q-tab name="IN" label="入库" @click="handleTabChange('IN')" />
            <q-tab name="OUT" label="出库" @click="handleTabChange('OUT')" />
            <q-tab name="INV" label="盘点" @click="handleTabChange('INV')" />
        </q-tabs>

        <div class="row justify-between q-mt-md">
            <div class="col-2">
                <q-input v-model="searchForm.docNo" label="单据号" dense outlined @keyup.enter="freshData" />
            </div>
            <div class="col-2">
                <q-select v-model="searchForm.productId" :options="productOptions" option-label="productName" dense outlined
                    option-value="id" emit-value map-options @update:model-value="freshData" clearable
                    label="商品名称" class="q-ml-md"/>
            </div>
            <div class="col-2">
                <q-select v-model="searchForm.warehouseId" :options="warehouseOptions" option-label="warehouseName" dense outlined
                    option-value="id" emit-value map-options @update:model-value="freshData" clearable
                    label="仓库名称" class="q-ml-md"/>
            </div>
            <q-space />
        </div>
        <q-table title="库存历史" :rows="rows" :columns="columns" row-key="name" :pagination="pagination" class="q-mt-md">
            <template v-slot:bottom>
                <div class="row col-12 justify-center q-mt-md">
                    <q-pagination v-model="pagination.page" :max="Math.ceil(pagination.rowsNumber / pagination.rowsPerPage)"
                     @update:model-value="freshData" input />
                </div>
            </template>
        </q-table>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue';
import axios from '@/axios/index';
import { useQuasar, type QTableColumn } from 'quasar';

const $q = useQuasar();

const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: 'left' },
    { name: 'remark', label: '单据号', field: 'remark', align: 'left' },
    { name: 'warehouseName', label: '仓库名称', field: 'warehouseName', align: 'left' },
    { name: 'productName', label: '商品名称', field: 'productName', align: 'left' },
    { name: 'diff', label: '操作数量', field: 'diff', align: 'left' },
    { name: 'actionUsername', label: '操作人', field: 'actionUsername', align: 'left' },
    { name: 'actionTime', label: '操作时间', field: 'actionTime', align: 'left' }
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
});

const tab = ref('IN');
const searchForm = reactive({
    productId: '',
    warehouseId: '',
    actionName: 'IN',
    docNo: ''
});

const productOptions = ref([]);
const warehouseOptions = ref([]);

const handleTabChange = (actionName: string) => {
    searchForm.actionName = actionName;
    pagination.value.page = 1;
    freshData();
}

const freshData = async () => {
    const res = await axios.get('/web/stockHistory/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            productId: searchForm.productId,
            warehouseId: searchForm.warehouseId,
            actionName: searchForm.actionName,
            docNo: searchForm.docNo
        }
    });
    if (res.data.code === 200) {
        rows.value = res.data.data.records.map((item: any, index: number) => ({
            ...item,
            index: (pagination.value.page - 1) * pagination.value.rowsPerPage + index + 1
        }));
        pagination.value.rowsNumber = res.data.data.total;
    }
    else {
        $q.notify({ message: res.data.message, color: 'negative', position: 'top', timeout: 2000 });
    }
}

const getProductOptions = async () => {
    const res = await axios.get('/web/product/list', { params: { current: 1, size: 10000 } });
    if (res.data.code === 200) {
        productOptions.value = res.data.data.records;
    }
}

const getWarehouseOptions = async () => {
    const res = await axios.get('/web/warehouse/list', { params: { current: 1, size: 10000 } });
    if (res.data.code === 200) {
        warehouseOptions.value = res.data.data.records;
    }
}

onMounted(() => {
    freshData();
    getProductOptions();
    getWarehouseOptions();
});
</script>

<style scoped>

</style>
