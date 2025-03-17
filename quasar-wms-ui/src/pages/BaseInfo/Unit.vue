<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <q-input v-model="queryunitName" label="单位名称" dense outlined @keyup.enter="freshData" />
            <q-space />
            <q-btn label="新增" icon="add" color="primary" @click="addUnit" />
        </div>
        <q-table title="单位管理" :rows="rows" :columns="columns" row-key="id" class="q-mt-md" :pagination="pagination"
            :rows-per-page-options="[10, 20, 50]" @update:pagination="onPaginationChange">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editUnit(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteUnit(props.row)">
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

        <!-- 新增/修改弹窗 -->
        <q-dialog v-model="dialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">{{ addUnitDialog ? '新增单位' : '修改单位' }}</div>
                    <q-form>
                        <q-input v-model="formData.unitName" label="单位名称" dense outlined
                            :rules="[val => !!val || '请输入单位名称']" />
                        <q-input v-model="formData.unitCode" label="统一社会信用代码/编码" dense outlined />
                    </q-form>
                </q-card-section>

                <q-card-actions align="right" class="text-primary">
                    <q-btn flat label="取消" v-close-popup />
                    <q-btn flat label="确定" @click="submitForm" />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar, type QTableColumn } from 'quasar';
import axios from '@/axios/index'

const $q = useQuasar();
const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: 'left', },
    { name: 'unitName', label: '单位名称', field: 'unitName', align: 'left' },
    { name: 'unitCode', label: '统一社会信用代码/编码	', field: 'unitCode', align: 'left' },
    { name: 'createdBy', label: '创建人', field: 'createdBy', align: 'left' },
    { name: 'createdTime', label: '创建时间', field: 'createdTime', align: 'left' },
    { name: 'updatedBy', label: '更新人', field: 'updatedBy', align: 'left' },
    { name: 'updatedTime', label: '更新时间', field: 'updatedTime', align: 'left' },
    { name: 'action', label: '操作', field: 'action', align: 'left' }
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10 ,
    rowsNumber: 0
});

const queryunitName = ref('');
const freshData = async () => {
    const res = await axios.get('/web/unit/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            unitName: queryunitName.value
        }
    });
    if (res.data.code === 200) {
        rows.value = res.data.data.records.map((item: any, index: number) => ({
            ...item,
            index: index + 1
        }));
        pagination.value.rowsNumber = res.data.data.total;
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
}

const onPaginationChange = (newPagination: any) => {
    pagination.value = newPagination;
    freshData();
};

const dialogVisible = ref(false);
const addUnitDialog = ref(false);

let formData = ref({
    unitName: '',
    unitCode: ''
});

const addUnit = () => {
    dialogVisible.value = true;
    addUnitDialog.value = true;
}

const editUnit = (row: any) => {
    dialogVisible.value = true;
    addUnitDialog.value = false;
    formData.value.unitName = row.unitName;
    formData.value.unitCode = row.unitCode;
}

const deleteUnit = (row: any) => {
    $q.dialog({
        title: '提示',
        message: '确定删除该单位吗？',
        cancel: true,
        persistent: true
    }).onOk(async () => {
        const res = await axios.get('/web/unit/delete', {
            params: {
                id: row.id
            }
        });
        if (res.data.code === 200) {
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000
            });
            freshData();
        } else {
            $q.notify({
                message: res.data.message,
                color: 'negative',
                position: 'top',
                timeout: 2000
            });
        }
    });
}

const submitForm = async () => {
    //进行表单验证
    if (!formData.value.unitName) {
        $q.notify({
            message: '请输入单位名称',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
        return;
    }
    const res = await axios.post('/web/unit/saveOrUpdate', formData.value);
    if (res.data.code === 200) {
        $q.notify({
            message: res.data.message,
            color: 'positive',
            position: 'top',
            timeout: 2000
        });
        freshData();
        dialogVisible.value = false;
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
}

onMounted(() => {
    freshData();
});

</script>

<style scoped></style>
