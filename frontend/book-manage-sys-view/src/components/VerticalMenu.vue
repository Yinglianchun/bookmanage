<template>
	<el-menu
		:collapse-transition="false"
		:collapse="flag"
		style="padding: 8px 18px;max-width: 253px;"
		:default-active="activeIndex"
		:background-color="bag"
		text-color="#aeb9d4"
		active-text-color="#fdfefe"
		@select="handleSelect">
		<el-menu-item v-for="(item, index) in routes" :key="index" style="width: 100%;"
			v-if="!item.isHidden" :index="item.path"
			:class="{ 'is-active': activeIndex === item.path }">
			<i :class="item.icon" style="font-size: 20px;"></i>
			<span slot="title" class="menu-text">{{ item.name }}</span>
		</el-menu-item>
	</el-menu>
</template>
<script>
export default {
	name: 'AdminMenu',
	data() {
		return {
			activeIndex: "1",
			isCollapse: true,
			selectedMenuItem: '',
		}
	},
	props: {
		routes: {
			type: Array,
			required: true
		},
		flag: {
			type: Boolean,
			required: true
		},
		bag: {
			type: String,
			default: '#FFFFFF'
		}
	},
	created(){
		// 上次选中路径
		const saveLastPath = sessionStorage.getItem('activeMenuItem');
		if(saveLastPath === null){
			// 加载首页
			this.handleSelect('/adminLayout');
		}else{
			this.handleSelect(saveLastPath);
		}
	},
	methods: {
		handleSelect(index) {
			this.activeIndex = index;
			this.$emit('select', this.activeIndex);
			sessionStorage.setItem('activeMenuItem', this.activeIndex);
		},
	},
};
</script>

<style scoped>
/* 全新的科技感深色侧边栏 */
::v-deep .el-menu {
    background: linear-gradient(160deg, rgba(6, 11, 27, 0.9) 0%, rgba(13, 18, 36, 0.85) 100%) !important;
    border: 1px solid rgba(255, 255, 255, 0.06) !important;
    border-radius: 18px !important;
    backdrop-filter: blur(26px) !important;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.55), inset 0 1px 0 rgba(255, 255, 255, 0.06) !important;
}

.menu-text {
	font-size: 14px;
	letter-spacing: 0.02em;
}

.is-active {
	background: linear-gradient(135deg, #5ef0d4 0%, #4b7bec 100%) !important;
	color: #ffffff !important;
	font-weight: 600 !important;
	border-radius: 14px !important;
	box-shadow:
        0 6px 16px rgba(79, 209, 197, 0.35),
        inset 0 1px 0 rgba(255, 255, 255, 0.3) !important;
	position: relative;
	overflow: hidden;

	&::before {
		content: '';
		position: absolute;
		inset: 0;
		background: linear-gradient(120deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0));
		border-radius: 14px;
	}
}

.el-menu-item,
.el-submenu__title {
	height: 48px !important;
	line-height: 48px !important;
	user-select: none;
	color: #aeb9d4 !important;
	border-radius: 14px !important;
	margin: 4px 10px !important;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
	font-weight: 500 !important;

	i {
		color: #7f8fb5 !important;
		transition: color 0.3s ease !important;
	}
}

.el-menu-item:focus,
.el-menu-item:hover {
	border-radius: 14px !important;
	background: rgba(94, 240, 212, 0.12) !important;
	color: #ffffff !important;
	transform: translateX(4px) !important;

	i {
		color: #5ef0d4 !important;
	}
}

.el-menu-item {
	position: relative;

	&:hover::before {
		content: '';
		position: absolute;
		left: 0;
		top: 50%;
		transform: translateY(-50%);
		width: 4px;
		height: 26px;
		background: linear-gradient(135deg, #5ef0d4 0%, #4b7bec 100%);
		border-radius: 2px;
	}
}

/* 滚动条样式 */
::v-deep .el-menu::-webkit-scrollbar {
	width: 4px;
}

::v-deep .el-menu::-webkit-scrollbar-track {
	background: rgba(255, 255, 255, 0.05);
	border-radius: 2px;
}

::v-deep .el-menu::-webkit-scrollbar-thumb {
	background: rgba(78, 205, 196, 0.5);
	border-radius: 2px;

	&:hover {
		background: rgba(78, 205, 196, 0.8);
	}
}
</style>
