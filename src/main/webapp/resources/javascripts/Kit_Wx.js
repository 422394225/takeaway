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
	getObj:function(name){
		try{
			var object = JSON.parse(localStorage.getItem(name));
			if(object==null){
				return new Object();
			}else {
                return object;
			}
		}catch(e){
			console.log(e);
			return new Object();
		}
	},
	setObj:function(name,obj){
		try{
			localStorage.setItem(name,JSON.stringify(obj));
		}catch(e){
			console.log(e);
		}
	},
    user:{
        get:function () {
            return WxStore.getObj("user");
        },
        set:function (obj) {
            WxStore.setObj("user",obj)
        },
        setParams:function(key,value){
            var user = WxStore.user.get();
            user[key] = value;
            WxStore.user.set(user);
        },
        getParams:function(key){
            var user = WxStore.user.get();
            return user[key];
        }
    },
    cart:{
        get:function () {
            return WxStore.getObj("cart");
        },
        set:function (obj) {
            WxStore.setObj("cart",obj)
        },
        setParams:function(key,value){
            var cart = WxStore.cart.get();
            cart[key] = value;
            WxStore.cart.set(cart);
        },
        getParams:function(key){
            var cart = WxStore.cart.get();
            return cart[key];
        }
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