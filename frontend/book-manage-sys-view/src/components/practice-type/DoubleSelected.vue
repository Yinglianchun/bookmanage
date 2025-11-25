<template>
    <div style="box-sizing: border-box;">
        <el-tabs v-model="activeName" style="min-height: 500px;">
            <el-tab-pane label="1. 完善题目" name="first">
                <Editor height="calc(100vh - 400px)" :receiveContent="operationData.askItem"
                    @on-receive="onReceiveContent" />
            </el-tab-pane>
            <el-tab-pane label="2. 设置选项" name="second">
                <div class="options-item" v-for="(question, index) in operationData.question" :key="index">
                    <span class="index-item">{{ question.type }}</span>
                    <input v-model="question.value" placeholder="选项描述" />
                </div>
            </el-tab-pane>
            <el-tab-pane label="3. 设置解析" name="third">
                <div class="options-item" v-for="(answer, index) in operationData.answer" :key="index">
                    <span :style="{ backgroundColor: isCheck(answer) ? '#a61b29' : '' }" @click="selected(answer)"
                        class="check-item"></span>
                    <span :style="{ color: isCheck(answer) ? '#a61b29' : '' }" class="index-item">{{
                        answer.type }}</span>
                    <input :style="{ color: isCheck(answer) ? '#a61b29' : '' }" v-model="answer.value"
                        placeholder="选项答案详解" />
                </div>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>
<script>
// 单选题组件
import Editor from "@/components/Editor"
export default {
    components: { Editor },
    props: {
        data: {
            type: Object,
            default: function () {
                return {};
            }
        },
    },
    watch: {
        data: {
            handler(newValue, oldValue) {
                this.operationData = newValue;
                this.$emit('on-listenner', newValue);
            },
            deep: true,
            immediate: true,
        },
    },
    data() {
        return {
            operationData: {},
            askItem: '',
            questionNumber: 1,
            answerNumber: 1,
            activeName: 'first',
        }
    },
    methods: {
        isCheck(answer) {
            return this.operationData.rightAnswer.find(type => type === answer.type) !== undefined;
        },
        selected(answer) {
            // 单选题，只能选一个
            const flag = this.operationData.rightAnswer.find(type => type === answer.type);
            if (flag !== undefined) {
                this.operationData.rightAnswer = this.operationData.rightAnswer.filter(type => type !== answer.type);
            } else {
                this.operationData.rightAnswer.push(answer.type);
            }
        },
        onReceiveContent(html) {
            this.operationData.askItem = html;
        },
    }
}

</script>
<style lang="scss" scoped>
.options-item {
    padding: 25px 0;
    border-bottom: 1px solid #f1f1f1;
    display: flex;
    align-items: center;
    flex-wrap: wrap;

    .index-item {
        display: inline-block;
        padding: 3px 10px;
        font-size: 16px;
        color: rgb(113, 114, 114);
    }

    .check-item {
        display: inline-block;
        width: 20px;
        height: 20px;
        border-radius: 10px;
        padding: 3px;
        box-sizing: border-box;
        border: 3px solid rgb(232, 234, 243);
        margin-right: 10px;
        transition: .8s;
    }

    input {
        padding: 5px 10px;
        outline: none;
        border: none;
        font-size: 16px;
        width: 80%;
    }
}
</style>