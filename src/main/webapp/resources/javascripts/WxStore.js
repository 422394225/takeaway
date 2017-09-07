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