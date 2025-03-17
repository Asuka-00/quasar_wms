<template>
    <div class="q-pa-md">
        <div class="row justify-between">
            <div class="col-2">
                <q-input v-model="search.categoryName" label="分类名称" dense outlined @keyup.enter="freshData" />
            </div>
            <q-space />
            <div>
                <q-btn label="新增" icon="add" color="primary" @click="addProductCategory" />
            </div>
        </div>
        <q-table title="商品分类管理" :rows="rows" :columns="columns" row-key="id" :pagination="pagination" class="q-mt-md">
            <template v-slot:body-cell-action="props">
                <q-td>
                    <q-btn flat round color="primary" icon="edit" size="sm" @click="editProductCategory(props.row)">
                        <q-tooltip>编辑</q-tooltip>
                    </q-btn>
                <q-btn flat round color="negative" icon="delete" size="sm" @click="deleteProductCategory(props.row)">
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
                    <div class="text-h6">{{ addProductCategoryDialog ? '新增分类' : '编辑分类' }}</div>
                    <q-form ref="formRef">
                        <q-input v-model="formData.categoryName" label="分类名称" dense outlined
                            :rules="[(val) => val.length > 0 || '分类名称不能为空']" />
                    </q-form>
                </q-card-section>
                <q-card-actions align="right">
                    <q-btn label="取消" color="negative" @click="dialogVisible = false; formData.categoryName = ''" />
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
    { name: 'categoryName', label: '分类名称', field: 'categoryName', align: "left" },
    { name: 'createdBy', label: '创建人', field: 'createdBy', align: "left" },
    { name: 'createdTime', label: '创建时间', field: 'createdTime', align: "left" },
    { name: 'updatedBy', label: '更新人', field: 'updatedBy', align: "left" },
    { name: 'updatedTime', label: '更新时间', field: 'updatedTime', align: "left" },
    { name: 'action', label: '操作', field: 'action', align: "left" },
]);

const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 0
});

const search = ref({
    categoryName: ''
});

const freshData = async () => {
    const res = await axios.get('/web/product_category/list', {
        params: {
            current: pagination.value.page,
            size: pagination.value.rowsPerPage,
            categoryName: search.value.categoryName
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
            timeout: 2000,
        });
    }
}

const dialogVisible = ref(false);
const addProductCategoryDialog = ref(false);

interface FormData {
    categoryName: string;
    id?: string | null;
}
const formData = ref<FormData>({
    categoryName: ''
});

const formRef = ref<QForm | null>(null);

const addProductCategory = () => {
    addProductCategoryDialog.value = true;
    dialogVisible.value = true;
    formData.value = {
        categoryName: ''
    };
}

const editProductCategory = (row: any) => {
    addProductCategoryDialog.value = false;
    dialogVisible.value = true;
    formData.value = {
        categoryName: row.categoryName,
        id: row.id
    };
}

const deleteProductCategory = (row: any) => {
    $q.dialog({
        title: '删除分类',
        message: '确定要删除该分类吗？',
        cancel: true,
        persistent: true,
    }).onOk(async () => {
        const res = await axios.get('/web/product_category/delete', {
            params: {
                id: row.id
            }
        });
        if (res.data.code === 200) {
            $q.notify({
                message: res.data.message,
                color: 'positive',
                position: 'top',
                timeout: 2000,
            });
            freshData();
        } else {
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
    if (!valid) {
        return;
    }
    const res = await axios.post('/web/product_category/saveOrUpdate', formData.value);
    if (res.data.code === 200) {
        $q.notify({
            message: res.data.message,
            color: 'positive',
            position: 'top',
            timeout: 2000,
        });
        dialogVisible.value = false;
        freshData();
    } else {
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
});

</script>

<style scoped></style>
