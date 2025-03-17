<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="queryDeptName" label="部门名称" dense outlined @keyup.enter="freshData" />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="queryUnitId" label="单位名称" dense outlined :options="unitOptions"
                    option-label="unitName" option-value="id" emit-value map-options @update:model-value="freshData"
                    clearable />
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addDepartment" />
            </div>
        </div>
        <q-table title="部门管理" :rows="rows" :columns="columns" row-key="id" class="q-mt-md"
            :pagination="pagination"
            :rows-per-page-options="[10, 20, 50]"
            @update:pagination="onPaginationChange">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editDepartment(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteDepartment(props.row)">
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
                    <div class="text-h6">{{ addDeptDialog ? '新增部门' : '编辑部门' }}</div>
                </q-card-section>
                <q-card-section>
                    <q-form>
                        <q-input v-model="formData.deptName" label="部门名称" dense outlined :rules="[(val) => val !== null || '部门名称不能为空']" />
                        <q-select v-model="formData.unitId" label="单位名称" dense outlined :options="unitOptions"
                            option-label="unitName" option-value="id" emit-value map-options :rules="[(val) => val !== null || '单位名称不能为空']" />
                    </q-form>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn label="取消" color="negative" @click="dialogVisible = false" />
                    <q-btn label="确定" color="primary" @click="submitForm" />
                </q-card-actions>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useQuasar, type QTableColumn } from 'quasar';
import axios from '@/axios/index';

const $q = useQuasar();
const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: 'left', },
    { name: 'deptName', label: '部门名称', field: 'deptName', align: 'left' },
    { name: 'unitName', label: '单位名称', field: 'unitName', align: 'left' },
    { name: 'createdBy', label: '创建人', field: 'createdBy', align: 'left' },
    { name: 'createdTime', label: '创建时间', field: 'createdTime', align: 'left' },
    { name: 'updatedBy', label: '更新人', field: 'updatedBy', align: 'left' },
    { name: 'updatedTime', label: '更新时间', field: 'updatedTime', align: 'left' },
    { name: 'action', label: '操作', field: 'action', align: 'left' }
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
});

const queryDeptName = ref('');

const queryUnitId = ref('');

const freshData = async () => {
    try {
        const res = await axios.get('/web/dept/list', {
            params: {
                current: pagination.value.page,
                size: pagination.value.rowsPerPage,
                deptName: queryDeptName.value,
                unitId: queryUnitId.value
            }
        });
        if (res.data.code === 200) {
            rows.value = res.data.data.records.map((item: any, index: number) => ({
                ...item,
                index: (pagination.value.page - 1) * pagination.value.rowsPerPage + index + 1
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
    } catch (error: any) {
        $q.notify({
            message: error.message || '获取数据失败',
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

const unitOptions = ref([]);
const getUnitOptions = async () => {
    const res = await axios.get('/web/unit/list', {
        params: {
            current: 1,
            size: 10000
        }
    });
    if (res.data.code === 200) {
        unitOptions.value = res.data.data.records;
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }
}


const dialogVisible = ref(false);
const addDeptDialog = ref(false);

interface IDepartmentForm {
    id?: number;
    deptName: string;
    unitId: number|undefined;
}

let formData = ref<IDepartmentForm>({
    deptName: '',
    unitId: undefined
});

const addDepartment = () => {
    dialogVisible.value = true;
    addDeptDialog.value = true;
    formData.value = {
        deptName: '',
        unitId: undefined
    };
}

const editDepartment = (row: any) => {
    dialogVisible.value = true;
    addDeptDialog.value = false;
    formData.value = {
        id: row.id,
        deptName: row.deptName,
        unitId: +row.unitId
    };
    console.log(formData.value);
    console.log(unitOptions.value);
}

const deleteDepartment = (row: any) => {
    $q.dialog({
        title: '提示',
        message: '确定删除该部门吗？',
        cancel: true,
        persistent: true
    }).onOk(async () => {
        const res = await axios.get('/web/dept/delete', {
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
    if (!formData.value.deptName) {
        $q.notify({
            message: '请输入部门名称',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
        return;
    }
    if (!formData.value.unitId) {
        $q.notify({
            message: '请选择单位名称',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
        return;
    }
    const res = await axios.post('/web/dept/saveOrUpdate', formData.value);
    if (res.data.code === 200) {
        $q.notify({
            message: res.data.message,
            color: 'positive',
            position: 'top',
            timeout: 2000
        });
        dialogVisible.value = false;
        freshData();
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
    getUnitOptions();
});



</script>

<style scoped></style>