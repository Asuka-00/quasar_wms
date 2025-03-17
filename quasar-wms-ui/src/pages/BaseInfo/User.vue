<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="queryVo.nickName" label="昵称" dense outlined @keyup.enter="onSearch" />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="queryVo.deptId" label="部门名称" dense outlined :options="deptOptions"
                    option-label="deptName" option-value="id" emit-value map-options @update:model-value="onSearch"
                    clearable />
            </div>
            <div class="col-2 q-ml-md">
                <q-select v-model="queryVo.unitId" label="单位名称" dense outlined :options="unitOptions"
                    option-label="unitName" option-value="id" emit-value map-options @update:model-value="onSearch"
                    clearable />
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addUser" />
            </div>

        </div>
        <q-table title="用户管理" :rows="rows" :columns="columns" row-key="id" class="q-mt-md"
            :pagination="pagination"
            :rows-per-page-options="[10, 20, 50]"
            @update:pagination="onPaginationChange">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editUser(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                    <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteUser(props.row)">
                        <q-tooltip>删除</q-tooltip>
                    </q-btn>
                </q-td>
            </template>
            <template v-slot:bottom>
                <div class="row col-12 justify-center q-mt-md">
                    <q-pagination v-model="pagination.page"
                        :max="Math.ceil(pagination.rowsNumber / pagination.rowsPerPage)" :max-pages="pagination.rowsPerPage"
                        @update:model-value="freshData" input />
                </div>
            </template>
        </q-table>

        <!-- 新增/编辑用户 -->
        <q-dialog v-model="dialogVisible" persistent>
            <q-card style="min-width: 550px">
                <q-card-section>
                    <div class="text-h6">{{ addUserDialog ? '新增用户' : '编辑用户' }}</div>
                    <q-form>
                        <q-input v-model="formData.nickname" label="昵称" dense outlined
                            :rules="[val => !!val || '请输入昵称']" />
                        <q-input v-model="formData.username" label="用户名" dense outlined
                            :rules="[val => !!val || '请输入用户名']" />
                        <q-select v-model="formData.role" label="角色" dense outlined :options="roleOptions"
                            option-label="roleName" option-value="id" emit-value map-options :rules="[val => !!val || '请选择角色']" />
                        <q-select v-model="formData.deptId" label="部门名称" dense outlined :options="deptOptions"
                            option-label="deptName" option-value="id" emit-value map-options :rules="[val => !!val || '请选择部门']" />
                        <q-input v-model="formData.email" label="邮箱" dense outlined
                            :rules="[val => !!val || '请输入邮箱']" type="email" />
                        <q-input v-model="formData.phone" label="手机号" dense outlined
                            :rules="[val => !!val || '请输入手机号']" />
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
import axios from '@/axios/index';

const $q = useQuasar();

const rows = ref([]);
const columns = ref<QTableColumn[]>([
    { name: 'index', label: '#', field: 'index', align: 'left', },
    { name: 'nickName', label: '昵称', field: 'nickName', align: 'left' },
    { name: 'userName', label: '用户名', field: 'userName', align: 'left' },
    { name: 'deptName', label: '部门名称', field: 'deptName', align: 'left' },
    { name: 'unitName', label: '单位名称', field: 'unitName', align: 'left' },
    { name: 'email', label: '邮箱', field: 'email', align: 'left' },
    { name: 'phone', label: '手机号', field: 'phone', align: 'left' },
    { name: 'role', label: '角色', field: 'role', align: 'left' },
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

interface UserQueryVo {
    deptId?: string;
    nickName?: string;
    unitId?: string;
}
const queryVo = ref<UserQueryVo>({
    nickName: '',
    deptId: '',
    unitId: ''
});

const freshData = async () => {
    try {
        const res = await axios.post('/web/user/list', {
            nickName: queryVo.value.nickName,
            deptId: queryVo.value.deptId,
            unitId: queryVo.value.unitId
        }, { params: { current: pagination.value.page, size: pagination.value.rowsPerPage } });

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

function onSearch() {
    pagination.value.page = 1;
    freshData();
}

const deptOptions = ref([]);
const getDeptOptions = async () => {
    const res = await axios.get('/web/dept/list', { params: { current: 1, size: 10000 } });
    if (res.data.code === 200) {
        deptOptions.value = res.data.data.records;
    } else {
        $q.notify({
            message: res.data.message,
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
    }

}

const unitOptions = ref([]);
const getUnitOptions = async () => {
    const res = await axios.get('/web/unit/list', { params: { current: 1, size: 10000 } });
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
const addUserDialog = ref(false);
const roleOptions = ref([
    { id: 'admin', roleName: '管理员' },
    { id: 'user', roleName: '普通用户' }
]);
interface FormData {
    id?: number;
    nickname: string;
    username: string;
    email: string;
    phone: string;
    role: string;
    deptId: number | undefined;
}
const formData = ref<FormData>({
    id: undefined,
    nickname: '',
    username: '',
    email: '',
    phone: '',
    role: '',
    deptId: undefined
});

const addUser = () => {
    dialogVisible.value = true;
    addUserDialog.value = true;
    formData.value = {
        id: undefined,
        nickname: '',
        username: '',
        email: '',
        phone: '',
        role: '',
        deptId: undefined
    }
}

const editUser = (row: any) => {
    dialogVisible.value = true;
    addUserDialog.value = false;
    formData.value = {
        id: row.id,
        nickname: row.nickName,
        username: row.userName,
        email: row.email,
        phone: row.phone,
        role: row.role,
        deptId: +row.deptId
    }
}

const deleteUser = (row: any) => {
    $q.dialog({
        title: '删除用户',
        message: '确定删除该用户吗？',
        cancel: true,
        persistent: true
    }).onOk(async () => {
        const res = await axios.post('/web/user/delete', {},{ params: { id: row.id } });
        if (res.data.code === 200) {
            $q.notify({
                message: '删除成功',
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
    if (!formData.value.nickname || !formData.value.username || !formData.value.email || !formData.value.phone || !formData.value.role || !formData.value.deptId) {
        $q.notify({
            message: '请输入完整信息',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
        return;
    }
    //校验邮箱，手机号是否符合格式
    if (!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.value.email)) {
        $q.notify({
            message: '邮箱格式不正确',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
        return;
    }
    if (!/^\d{11}$/.test(formData.value.phone)) {
        $q.notify({
            message: '手机号格式不正确',
            color: 'negative',
            position: 'top',
            timeout: 2000
        });
        return;
    }
    //提交表单
    const res = await axios.post('/web/user/saveOrUpdate', formData.value);
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

const onPaginationChange = (newPagination: any) => {
    pagination.value = newPagination;
    freshData();
};

onMounted(() => {
    getDeptOptions();
    getUnitOptions();
    freshData();
});
</script>

<style scoped></style>
