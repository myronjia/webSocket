var v = window.parent.vue;
axios.defaults.withCredentials = true;


let loading = null;
// let LoadingObj = this.$loading;
function openShade({vue,text}){
    // v.$loading.show();
    if(!vue) vue= v;
    loading=vue.$loading({
        lock: true,
        text: text
        //,spinner: 'el-icon-loading'
        //,background: 'rgba(0, 0, 0, 0.7)'
    });
}
function closeShade({vue}){
    if(!vue) vue= v;
    if(loading){
        vue.$nextTick(() => { // 以服务的方式调用的 Loading 需要异步关闭
            loading.close();
            loading = null;
        });
    }
}

function cp(search) {
    var clone ={};
    for(var field in search) {
        if(typeof search[field] === 'object' && !isNaN(search[field].length)){
            if(!clone[field]) clone[field] = [];
            for(var i=0;i<search[field].length;i++){
                clone[field][i] = {...{},...search[field][i]};
            }
        } else{
            clone[field] = {...{},...search[field]};
        }
    }
    return clone;
}
function addRule (key,item) {
    if(item.data != null && item.data!=''){
        return {
            field:key,
            data:item.data,
            op:item.op
        };
    }
    else{
        return null;
    }
}
function search2Cond(search,groupOp){
    let cond ={
        rules:[],
        groupOp:groupOp
    }
    for(let field in search) {
        if(typeof search[field] === 'object' && !isNaN(search[field].length)){
            for(var i=0;i<search[field].length;i++){
                var rule  = addRule(field,search[field][i]);
                if(rule!=null) cond.rules.push(rule);
            }
        } else{
            var rule = addRule(field,search[field]);
            if(rule!=null) cond.rules.push(rule);
        }
    }
    return cond;
}


function loadTable({el,tableHeight,data,methods,mounted,dialogIndex,urlList,urlEdit,urlChangeOrder,urlExport,urlImport,dialogWidth,dialogHeight,urlDel,title,fullscreen}){
   return new Vue({
        el: el?el:'#app',
        data:{
            ...{
                loading:false,
                button:[],
                //表格当前页数据
                tableData : [],
                //默认每页数据量
                pagesize : 10,
                    //当前页码
                currentPage : 1,
                    //查询的页码
                start : 1,
                //默认数据总数
                totalCount: 0,
                search: {},
                resetSearch:{},
                multipleSelection: [],
                tableHeight:tableHeight?tableHeight:window.innerHeight-150
            }, ...data
        },
        mounted: function() {
            this.resetSearch = cp(this.search);
            this.loadData(this.currentPage, this.pagesize);
            if(mounted) mounted(this);
        },
        methods: {
            ...{
                afterLoadData(res){

                },
                customIndex(index){
                     return (this.currentPage-1)*this.pagesize+index+1;
                },
                //从服务器读取数据
                async loadData(pageNum, pageSize) {
                    openShade({vue:this,text:"数据加载中，请稍后。。。"});
                    let {data} = await
                    axios.post(urlList, {
                        "pageable": {
                            'current': pageNum,
                            'size': pageSize
                        }, "cond": search2Cond(this.search, "AND")
                    });

                    if (data.code == 0) {
                        this.tableData = data.body.items;
                        this.totalCount = data.body.total;
                        this.afterLoadData(data.body);
                    }
                    else if(data.code == -1009){
                        if(window.parent){
                            window.parent.vue.reLogin();
                        }
                    }
                    else {
                        v.$message({type: 'error', message: data.message});
                    }

                    closeShade({vue:this});
                },
                reload(){
                    this.loadData(this.currentPage, this.pagesize);
                },
                handleReset(){
                    this.search = cp(this.resetSearch);
                    this.reload();
                },
                //每页显示数据量变更
                handleSizeChange(val) {
                    this.pagesize = val;
                    this.reload();
                },
                //页码变更
                handleCurrentChange(val) {
                    this.currentPage = val;
                    this.reload();
                },
                //搜索
                handleSearch() {
                    this.currentPage = 1;
                    this.reload();
                },
                handleAdd() {
                    v.dialogShow(this.getDialogIndex(),{title:'添加'+title,url:urlEdit,width:dialogWidth,height:dialogHeight,fullscreen:fullscreen});
                },
                handleEdit(index, row) {
                  let edit='';
                    if(urlEdit.indexOf("?")>-1){
                        edit = urlEdit+"&id="+row.id;
                    }
                    else{
                        edit = urlEdit+"?id="+row.id;
                    }
                    v.dialogShow(this.getDialogIndex(),{title:'编辑'+title,url:edit,width:dialogWidth,height:dialogHeight,fullscreen:fullscreen});
                },
                handleDel(index, row) {
                    //window.parent
                    this.delById(row.id);
                },
                handleExport(){

                    let cond = JSON.stringify(search2Cond(this.search, "AND"));
                    let tempUrl = encodeURI(urlExport+"?cond="+cond+"&timeStamp="+new Date());
                    //v.export(urlExport+"?d="+new Date()+"&cond="+cond);
                    v.export(tempUrl);

                },
                handleImport(iframe,duration){
                    v.import(urlImport+"?d="+new Date(),iframe,duration);
                },
                handleDelBatch() {
                    //window.parent
                    if(this.multipleSelection.length==0){
                        v.$message({type: 'error', message: '至少选择一项进行删除'});
                        return;
                    }
                    let ids = this.multipleSelection.map(r=>r.id).join(',');
                    this.delById(ids);
                },
                handleSelectionChange(val) {
                    this.multipleSelection = val;
                },
                async handleChangeOrder(id,up){
                    let {data} = await axios.post(urlChangeOrder+"/"+id+"/"+up);
                    if (data.code == 0) {
                        this.$message({type: 'success', message: '移动成功!'});
                        this.reload();
                    }
                    else if(data.code == -1009){
                        if(window.parent){
                            window.parent.vue.reLogin();
                        }
                    }
                    else {
                        this.$message({type: 'error', message: data.message});

                    }
                },
                async delById(ids){
                    let r = await v.$confirm('此操作将删除该' + title + ', 是否继续?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'}).catch(() => {});
                    if(r=='confirm') {
                        openShade({vue:this,text:"数据处理中，请稍后。。。"});
                        let {data} = await axios.delete(urlDel + ids);
                        if (data.code == 0) {
                            this.$message({type: 'success', message: '删除成功!'});
                            this.reload();
                        }
                        else if(data.code == -1009){
                            if(window.parent){
                                window.parent.vue.reLogin();
                            }
                        }
                        else {
                            this.$message({type: 'error', message: data.message});

                        }
                        closeShade({vue:this});
                    }
                },
                authentication(id){
                    return this.button.includes(id);
                },
                getDialogIndex(){
                    if(dialogIndex) return dialogIndex;
                    else return 0;
                },
                async postData({title,url,parms,no_reload}){
                    if(title) openShade({vue:this,text:title});
                    let {data} = await axios.post(url, parms);
                    if (data.code == 0) {
                        if(no_reload) return;
                        this.reload();
                    }
                    else if(data.code == -1009){
                        if(window.parent){
                            window.parent.vue.reLogin();
                        }
                    }
                    else {
                        v.$message.error(data.message);
                        valid = false;
                    }
                    if(title) closeShade({vue:this});
                }
            },
            ...methods
        }

    });
}

function loadTree({el,data,methods,mounted,dialogIndex,setting,urlList,urlEdit,dialogWidth,dialogHeight,urlDel,title,fullscreen}){
    return new Vue({
                el: el?el:'#app',
                data:{
                    ...{
                        loading:false,
                        button:[],
                        setting:{...{
                            data: {
                                simpleData: {
                                    enable: true
                                },
                                key:{
                                    name:'text'
                                },
                                view: {
                                    showLine: true
                                }
                            },
                            callback: {
                                onClick(e, treeId, treeNode, clickFlag) {
                                    $.fn.zTree.getZTreeObj(treeId).checkNode(treeNode, !treeNode.checked, true);
                                }
                            }
                        },...setting}, ...data
                    }
                },
                mounted(){
                    this.loadData();
                    if(mounted) mounted(this);
                },
                methods:{...{
                    async loadData(){
                        openShade({vue:this,text:"数据加载中，请稍后。。。"});
                        if(this.setting.async){
                            $.fn.zTree.init($("#"+this.treeName), this.setting);
                        }
                        else{
                            let {data} = await axios.post(urlList, {
                                "cond": search2Cond(this.search, "AND")
                            });
                            if (data.code == 0) {
                                $.fn.zTree.init($("#"+this.treeName), this.setting, data.body);
                                //.expandAll(true);
                            }
                            else if(data.code == -1009){
                                if(window.parent){
                                    window.parent.vue.reLogin();
                                }
                            }
                            else {
                                //v.$message.error(data.message);
                                $.fn.zTree.init($("#"+this.treeName), this.setting, null);
                            }
                        }

                        closeShade({vue:this});
                    },
                    reload(){
                        this.loadData();
                    },
                    handleAddRoot(){
                        v.dialogShow(this.getDialogIndex(),{title:'添加'+title,url:urlEdit,width:dialogWidth,height:dialogHeight,fullscreen:fullscreen});
                    },
                    handleAdd(){
                        let node = this.selection();
                        if(node!=null){
                            v.dialogShow(this.getDialogIndex(),{title:'添加'+title,url:urlEdit+"?idParent="+node.id+"&gradationCode="+node.attr.gradationCode,width:dialogWidth,height:dialogHeight,fullscreen:fullscreen});
                        }
                    },
                    handleEdit(){
                        let node = this.selection();
                        if(node!=null){
                            v.dialogShow(this.getDialogIndex(),{title:'编辑'+title,url:urlEdit+"?id="+node.id+"&gradationCode="+node.attr.gradationCode,width:dialogWidth,height:dialogHeight,fullscreen:fullscreen});
                        }
                    },
                    async handleDel(){
                        let node = this.selection(true,false);
                        if(node!=null){
                            let r = await v.$confirm('此操作将删除该' + title + ', 是否继续?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'}).catch(() => {});
                            if(r=='confirm') {
                                openShade({vue:this,text:"数据加载中，请稍后。。。"});
                                let {data} = await axios.delete(urlDel + node.id);
                                if (data.code == 0) {
                                    this.$message({type: 'success', message: '删除成功!'});
                                    this.tree().removeNode(node);
                                }
                                else if(data.code == -1009){
                                    if(window.parent){
                                        window.parent.vue.reLogin();
                                    }
                                }
                                else {
                                    this.$message({type: 'error', message: data.message});

                                }
                                closeShade({vue:this});
                            }
                        }
                    },
                    selection(leaf,isSelect){
                        let treeObj = this.tree();
                        let nodes = isSelect ? treeObj.getCheckedNodes() : treeObj.getSelectedNodes();
                        if(nodes==null || nodes.length==0){
                            v.$message.error("必须选中一项才能操作");
                        }
                        else if(leaf && nodes[0].children && nodes[0].children.length>0){
                            v.$message.error("必须选中叶节点才能操作");
                        }
                        else{
                            return nodes[0];
                        }
                    },
                    tree(){
                        return $.fn.zTree.getZTreeObj(this.treeName);
                    },
                    authentication(id){
                        return this.button.includes(id);
                    },
                    getDialogIndex(){
                        if(dialogIndex) return dialogIndex;
                        else return 0;
                    }
                },...methods}
    });
}

function loadForm({el,data,methods,mounted,dialogIndex,formData,rules,urlSave,urlImport,iframe}){
    return new Vue({
        el: el?el:'#app',
        data() {
            return {
                ...{
                    loading:false,
                    formData:formData,
                    rules: rules
                },...data
            }
        },
        mounted(){
            if(mounted) mounted(this);
        },
        methods: {
            ...{
                beforeValid(){  },
                beforeSave(){ return true; },
                async afterSave(data,iframe){
                    let valid = true;

                    if (data.code == 0) {
                        window.parent.frames[iframe].vue.reload();
                    }
                    else if(data.code == -1009){
                        if(window.parent){
                            window.parent.vue.reLogin();
                        }
                    }
                    else {
                        v.$message.error(data.message);
                        valid =  false;
                    }
                    return valid;
                    //closeShade({vue:this});
                },
                async add(){
                    let flag = await this.addOrSave();
                    if(flag) this.$refs['formData'].resetFields();
                },
                async addOrSave(url){
                    this.beforeValid();
                    let valid = await this.$refs['formData'].validate().catch(() => { return false;});
                    if(valid){
                        if(!this.beforeSave()) return;
                        openShade({vue:this,text:"数据保存中，请稍后。。。"});
                        let {data} = await axios.post(url?url:urlSave, this.formData);
                        valid = await this.afterSave(data,iframe);
                    }
                    closeShade({vue:this});
                    return valid;
                },
                async save(url){
                    let flag = await this.addOrSave(url);
                    if(flag) this.close();
                },
                handleImport(iframe,duration){
                    v.import(urlImport,iframe,duration);
                },
                close(){
                    let that = this;
                    setTimeout(function () { v.dialogHide(that.getDialogIndex()); }, 0);
                },
                getDialogIndex(){
                    if(dialogIndex) return dialogIndex;
                    else return 0;
                }
            },
            ...methods
        }

    });
}