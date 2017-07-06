var Kit={
	post:function(u,d,successFunc,failFunc,option){
		var defaultOption = {
			type:'POST',
			data: d,
			url: u,
			dataType:'json',
			success:function(data){
				if(data.error){
					failFunc(data.msg,data.data);
				}else{
					successFunc(data.msg,data.data);
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				try{
					layer.msg("服务器响应失败！");
				}catch(e){
					alert("服务器响应失败！");
				}
				console.log("XMLHttpRequest：\t");
				console.log(XMLHttpRequest);
				console.log("\ntextStatus：\t");
				console.log(textStatus);
				console.log("\nerrorThrown：\t");
				console.log(errorThrown);
			}
		};
		if(option!=undefined){
			$.extend(defaultOption,option)
		};
		$.ajax(defaultOption);
	},
	get:function(u,d,successFunc,failFunc,option){
		var defaultOption = {
			type:'GET',
			data: d,
			url: u,
			success:function(data){
				if(data.error){
					params.failFunc(data.msg,data.data);
				}else{
					params.successFunc(data.msg,data.data);
				}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				try{
					layer.msg("服务器响应失败！");
				}catch(e){
					alert("服务器响应失败！");
				}
				console.log("XMLHttpRequest：\t");
				console.log(XMLHttpRequest);
				console.log("\ntextStatus：\t");
				console.log(textStatus);
				console.log("\nerrorThrown：\t");
				console.log(errorThrown);
			}
		}
		if(option!=undefined){
			$.extend(defaultOption,option)
		};
		$.ajax(defaultOption);
	},
	form:function(params){
		$(params.formId).bootstrapValidator({
 			submitButtons: params.btnId,
            feedbackIcons: false,
          //excluded:[":hidden",":disabled",":not(visible)"] ,//bootstrapValidator的默认配置
            excluded:[":disabled"],//关键配置，表示只对于禁用域不进行验证，其他的表单元素都要验证
            fields: params.validate
        });
		$(params.btnId).click(function(){
			 submit();
		});
		$(params.formId).keydown(function(event) {  
	       if(event.keyCode == 13) { 
	    	   submit();
	       }  
		});
		function submit(){
			if(params.beforeSubmit!=undefined){
				params.beforeSubmit();
			}
			var bootstrapValidator = $(params.formId).data('bootstrapValidator');
			bootstrapValidator.validate();
			if(bootstrapValidator.isValid())
			{
				var action = $(params.formId).attr("action");
				layer.load(1, {shade: [0.3,'#000']});
				$(params.formId).ajaxSubmit({
					type:'POST',
					url: action,
					dataType:'json',
					success:function(data){
						layer.closeAll('loading');
//						console.log(data)
						if(data){
							$(params.btnId).removeAttr("disabled");
							if(data.error){
								params.failFunc(data.msg,data.data);
							}else{
								params.successFunc(data.msg,data.data);
							}
						}
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
						layer.closeAll('loading');
						try{
							layer.msg("服务器响应失败！");
						}catch(e){
							alert("服务器响应失败！");
						}
						console.log("XMLHttpRequest：\t");
						console.log(XMLHttpRequest);
						console.log("\ntextStatus：\t");
						console.log(textStatus);
						console.log("\nerrorThrown：\t");
						console.log(errorThrown);
					}
				});
			}
			else return;
		}
	},
	table:function(tableId,option){
		var deafaultOption = {
			"processing": true,//开启服务器负责处理数据
	        "serverSide": true,//开启服务器负责处理数据
	        "searching":false,
	        searchable:false,
			pagingType: "full_numbers",//分页样式的类型
			"bLengthChange": true,  ///是否可以修改页面显示行数
			"aLengthMenu": [[10, 25, 50, -1], ['10条', '25条', '50条', '全部']], ///设置可选的显示行数
			"iDisplayLength": 10,  ///默认显示20行
			 "fnDrawCallback":function(){
				 if(option.fColumnInit==undefined || option.fColumnInit){
					 var api =this.api();
		                var startIndex= api.context[0]._iDisplayStart;        //获取到本页开始的条数 　
		                 api.column(0).nodes().each(function(cell, i) {
		                        cell.innerHTML = startIndex + i + 1;
		               });
				 }
             },
             "oLanguage": {//语言设置  
                 "sLengthMenu": "每页显示 _MENU_ 记录",     
                 "sZeroRecords": "没有检索到数据",     
                 "sInfo": "当前显示 _START_ 到 _END_ 条，共 _TOTAL_ 条记录", 
                 "sInfoEmpty": "当前没有数据可显示",//筛选为空时左下角的显示。
                 "sInfoFiltered": "(从 _MAX_条数据中检索)",//筛选之后的左下角筛选提示，
                 "sInfoEmtpy": "没有数据",     
                 "sProcessing": '<i class="fa fa-coffee"></i> 正在加载数据...',  
//                 "sSearch":"<i class='fa fa-search'></i>",
// 				 "sSearchPlaceholder":"搜索...",
                 "oPaginate": {     
                     "sFirst": "<i class='fa fa-angle-double-left'></i>",     
                     "sPrevious": "<i class='fa fa-angle-left'></i>",     
                     "sNext": "<i class='fa fa-angle-right'></i>",     
                     "sLast": "<i class='fa fa-angle-double-right'></i>"
                 }
 			 }
		}
		$.extend(deafaultOption,option);
		deafaultOption.columns = [];//清空columns
		var nullColumns =  [{ "data": null }];
		var tagets=[];
		if(option.fColumnNull==undefined || option.fColumnNull){
			$.merge(deafaultOption.columns,nullColumns);
			$.merge(tagets,[0]);
		}
		$.merge(deafaultOption.columns,option.columns);
		if(option.lColumnNull==undefined || option.lColumnNull){
			$.merge(deafaultOption.columns,nullColumns);
			$.merge(tagets,[-1]);
		}
		if(tagets.length>0){
			$.merge(deafaultOption.columnDefs,[{
	            "targets": tagets,//第一栏和最后一栏不进行排序
	            "bSortable":false
	        }]);
		}
		//------------------筛选框--------------------------------
		var searchParams=[];
		if($(".filterTable").length>0){
			var filterTableParams = $(".filterTable input[name]");
			//绑定筛选按钮事件
			$(".dataTableSortBtn").on("click",function(){
				searchParams=[];
				for(var i=0;i<filterTableParams.length;i++){
					var obj={};
					var value = $(filterTableParams[i]).val();
					if(value!="" && value!=null){
						var key = $(filterTableParams[i]).attr("name");
						obj[key] = value;
						searchParams.push(obj);
					}
				}
				table.ajax.reload(null, false);
			})
			//绑定回车键
			filterTableParams.keydown(function(event) {  
		       if(event.keyCode == 13) { 
		    	   $(".dataTableSortBtn").click();
		       }  
			});
			//绑定参数
			var ajaxUrl = deafaultOption.ajax;
			deafaultOption.ajax = {};
			deafaultOption.ajax["url"] = ajaxUrl;
			deafaultOption.ajax["data"] = {"custom_search":function(){
				return JSON.stringify(searchParams);
			}};
			
		}
		var table = $(tableId).DataTable(deafaultOption);
		//-----------------悬浮行样式Start-------------------------------
		var lastIdx = null;
		$(tableId+' tbody')
        .on('mouseover','tr',function(){
            var colIdx = table.row(this).index();
            if (colIdx !== lastIdx) {
                $(table.rows().nodes()).removeClass('info');
                $(table.row(colIdx).nodes()).addClass('info');
            }
        })
        .on('mouseleave',function(){
            $(table.rows().nodes()).removeClass('info');
        })
		//-----------------悬浮行样式End-------------------------------
		return table;
	}
}
$.fn.fileupload = function(option){
	var defaultOption = {
		language : 'zh',
		showUpload: false,
		showCaption: false,
		browseClass: "btn btn-info",
		removeLabel: "清空",
	    removeClass: "btn btn-danger",
		preferIconicPreview: true,
		previewFileIconSettings: {
	        'doc': '<i class="fa fa-file-word-o"></i>',
	        'xls': '<i class="fa fa-file-excel-o"></i>',
	        'ppt': '<i class="fa fa-file-powerpoint-o"></i>',
	        'pdf': '<i class="fa fa-file-pdf-o"></i>',
	        'zip': '<i class="fa fa-file-archive-o"></i>',
	        'htm': '<i class="fa fa-file-code-o"></i>',
	        'txt': '<i class="fa fa-file-text-o"></i>',
	        'mov': '<i class="fa fa-file-movie-o"></i>',
	        'mp3': '<i class="fa fa-file-audio-o"></i>'
	    },
	    previewFileExtSettings: {
	        'doc': function(ext) {
	            return ext.match(/(doc|docx)$/i);
	        },
	        'xls': function(ext) {
	            return ext.match(/(xls|xlsx)$/i);
	        },
	        'ppt': function(ext) {
	            return ext.match(/(ppt|pptx)$/i);
	        },
	        'zip': function(ext) {
	            return ext.match(/(zip|rar|tar|gzip|gz|7z)$/i);
	        },
	        'htm': function(ext) {
	            return ext.match(/(htm|html)$/i);
	        },
	        'txt': function(ext) {
	            return ext.match(/(txt|ini|csv|java|php|js|css)$/i);
	        },
	        'mov': function(ext) {
	            return ext.match(/(avi|mpg|mkv|mov|mp4|3gp|webm|wmv)$/i);
	        },
	        'mp3': function(ext) {
	            return ext.match(/(mp3|wav)$/i);
	        }
	    }
	};
	$.extend(option,defaultOption);
	$(this).fileinput(option);
}