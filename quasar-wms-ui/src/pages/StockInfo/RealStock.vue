<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-select v-model="searchForm.warehouseId" :options="warehouseOptions" option-label="warehouseName" dense outlined
                    option-value="id" emit-value map-options @update:model-value="freshData" clearable
                    label="仓库名称" />
            </div>
            <div class="col-2">
                <q-select v-model="searchForm.productId" :options="productOptions" option-label="productName" dense outlined
                    option-value="id" emit-value map-options @update:model-value="freshData" clearable
                    label="商品名称" class="q-ml-md"/>
            </div>
            <q-space />
        </div>
        <q-table title="实时库存" :rows="rows" :columns="columns" row-key="id" :pagination="pagination" class="q-mt-md">
            <template v-slot:body-cell="props">
                <q-td :props="props" :class="getRowClass(props.row)">
                    {{ props.value }}
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
    { name: 'warehouseName', label: '仓库名称', field: 'warehouseName', align: 'left' },
    { name: 'productName', label: '商品名称', field: 'productName', align: 'left' },
    { name: 'quantity', label: '库存数量', field: 'quantity', align: 'left' },
    { name: 'minStock', label: '最小库存', field: 'minStock', align: 'left' },
    { name: 'maxStock', label: '最大库存', field: 'maxStock', align: 'left' },
    { name: 'createdBy', label: '创建人', field: 'createdBy', align: 'left' },
    { name: 'createdTime', label: '创建时间', field: 'createdTime', align: 'left' },
    { name: 'updatedBy', label: '更新人', field: 'updatedBy', align: 'left' },
    { name: 'updatedTime', label: '更新时间', field: 'updatedTime', align: 'left' },
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
});

const searchForm = reactive({
    productId: '',
    warehouseId: '',
});

const freshData = async () => {
    const res = await axios.post('/web/realStock/list', {
        productId: searchForm.productId,
        warehouseId: searchForm.warehouseId,
    }, {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
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
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
}

const warehouseOptions = ref([]);
const productOptions = ref([]);

const getWarehouseOptions = async () => {
    const res = await axios.get('/web/warehouse/list', { params: { current: 1, size: 10000 } });
    if (res.data.code === 200) {
        warehouseOptions.value = res.data.data.records;
    }
    else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
}


const getProductOptions = async () => {
    const res = await axios.get('/web/product/list', { params: { current: 1, size: 10000 } });
    if (res.data.code === 200) {
        productOptions.value = res.data.data.records;
    }
    else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
}

const getRowClass = (row: any) => {
    if (row.maxStock !== null || row.minStock !== null) {
        if (+row.quantity > +row.maxStock || +row.quantity < +row.minStock) {
            return 'bg-negative-1';
        }
    }
    return '';
};

onMounted(() => {
    freshData();
    getWarehouseOptions();
    getProductOptions();
});
</script>

<style scoped>
.bg-negative-1 {
    background-color: #ffebee !important;
}
</style>
