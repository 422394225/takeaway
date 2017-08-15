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
					$.alert("服务器响应失败！");
				}catch(e){
					alert("服务器响应失败！");
				}
				console.log("XMLHttpRequest：\t");
				console.log(XMLHttpRequest);
				console.log("textStatus：\t"+textStatus);
				console.log("errorThrown：\t");
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
					$.alert("服务器响应失败！");
				}catch(e){
					alert("服务器响应失败！");
				}
				console.log("XMLHttpRequest：\t");
				console.log(XMLHttpRequest);
				console.log("textStatus：\t"+textStatus);
				console.log("errorThrown：\t");
				console.log(errorThrown);
			}
		}
		if(option!=undefined){
			$.extend(defaultOption,option)
		};
		$.ajax(defaultOption);
	}
}
var WxStore = {
	getUser:function(){
		try{
			return JSON.parse(localStorage.getItem("user"));
		}catch(e){
			console.log(e);
			return new Object();
		}
	},
	setUser:function(user){
		try{
			localStorage.setItem("user",JSON.stringify(user));
		}catch(e){
			console.log(e);
		}
	},
	setParams:function(key,value){
		var user = this.getUser();
		user[key] = value;
		this.setUser(user);
	},
	getParams:function(key){
		var user = this.getUser();
		return user.key;
	}
}
function getProjectName(){
    var curWwwPath=window.document.location.href;
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    var localhostPaht=curWwwPath.substring(0,pos);
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return projectName;
}
//val不能被bootstrapvalidator触发的bug
$.fn.changeVal = function(v,f){
	$(this).val(v);
    $(this).parents("form:first").bootstrapValidator('updateStatus', f, 'VALID');
    return this;
};