<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="search.warehouseName" label="仓库名称" dense outlined />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="search.unitId" label="所属单位" dense outlined :options="unitOptions"
                    option-label="unitName" option-value="id" emit-value map-options clearable @update:model-value="freshData"/>
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addWareHouse" />
            </div>
        </div>
        <q-table title="仓库管理" :rows="rows" :columns="columns" row-key="id" class="q-mt-md"
            :pagination="pagination"
            :rows-per-page-options="[10, 20, 50]"
            @update:pagination="onPaginationChange">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editWareHouse(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteWareHouse(props.row)">
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

        <q-dialog v-model="dialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">{{ addWareHouseDialog ? '新增仓库' : '编辑仓库' }}</div>
                    <q-form ref="formRef">
                        <q-input v-model="formData.warehouseName" label="仓库名称" dense outlined :rules="[(val) => val.length > 0 || '仓库名称不能为空']"/>
                        <q-select v-model="formData.unitId" label="所属单位" dense outlined :options="unitOptions"
                            option-label="unitName" option-value="id" emit-value map-options :rules="[(val) => val.id !== '' || '所属单位不能为空']"/>
                        <q-input v-model="formData.address" label="仓库地址" dense outlined :rules="[(val) => val.length > 0 || '仓库地址不能为空']"/>
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
import axios from '@/axios/index';
import { useQuasar, type QTableColumn } from 'quasar';

const $q = useQuasar();

const rows = ref([]);
const columns = ref<QTableColumn[]>([
    {name: 'index',label: '#',field: 'index',required: true,align: 'left'},
    {name: 'warehouseName',label: '仓库名称',field: 'warehouseName',required: true,align: 'left'},
    {name: 'unitName',label: '所属单位',field: 'unitName',required: true,align: 'left'},
    {name: 'address',label: '仓库地址',field: 'address',required: true,align: 'left'},
    {name: 'createdBy',label: '创建人',field: 'createdBy',required: true,align: 'left'},
    {name: 'createdTime',label: '创建时间',field: 'createdTime',required: true,align: 'left'},
    {name: 'updatedBy',label: '更新人',field: 'updatedBy',required: true,align: 'left'},
    {name: 'updatedTime',label: '更新时间',field: 'updatedTime',required: true,align: 'left'},
    {name: 'action',label: '操作',field: 'action',required: true,align: 'left'},
]);
const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
});
const search = ref({
    warehouseName: '',
    unitId: '',
});


const freshData = async () => {
    const res = await axios.get('/web/warehouse/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            warehouseName: search.value.warehouseName,
            unitId: search.value.unitId,
        }
    });
    if (res.data.code === 200) {
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

const unitOptions = ref([]);

const getUnitOptions = async () => {
    const res = await axios.get('/web/unit/list',{params:{
        current: 1,
        size: 10000,
    }});
    unitOptions.value = res.data.data.records;
}

const onPaginationChange = (newPagination: any) => {
    pagination.value = newPagination;
    freshData();
};

const dialogVisible = ref(false);
const addWareHouseDialog = ref(false);

interface FormData {
    id?: number;
    warehouseName: string;
    unitId: number|null;
    address: string;
}
const formData = ref<FormData>({
    warehouseName: '',
    unitId: null,
    address: '',
});
const formRef = ref<any>(null);

const addWareHouse = () => {
    addWareHouseDialog.value = true;
    dialogVisible.value = true;
}

const editWareHouse = (row: any) => {
    addWareHouseDialog.value = true;
    dialogVisible.value = true;
    formData.value.id = row.id;
    formData.value.warehouseName = row.warehouseName;
    formData.value.unitId = +row.unitId;
    formData.value.address = row.address;
}

const deleteWareHouse = async (row: any) => {
    $q.dialog({
        title: '删除仓库',
        message: '确定要删除该仓库吗？',
        cancel: true,
        persistent: true,
    }).onOk(async () => {
        const res = await axios.post('/web/warehouse/delete',{},{params:{id:row.id}});
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
    });
}

const submitForm = async () => {
    // 校验表单
    const valid = await formRef.value?.validate();
    if(!valid){
        return;
    }
    const res = await axios.post('/web/warehouse/saveOrUpdate',formData.value);
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
    getUnitOptions();
});
</script>

<style scoped>

</style>
