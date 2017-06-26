var TreeKit = {
	menuInit:function(domId,defaultData,options){
		var defaultOptions = {
			data: defaultData,
			showIcon: true,
			highlightSelected:true,
			multiSelect:true,
			selectedBackColor: "#04BF9A",
			onNodeSelected: function(event, node) {
				 var selectNodes = TreeKit.getChilrdNode($menuTree.selector,node);//获取所有子节点
			     if(selectNodes){ //子节点不为空，则选中所有子节点
			    	 var parentNodes = TreeKit.getParentNode($menuTree.selector,node);//同时选择父节点
			    	 if(parentNodes){
			    		 $.merge(selectNodes,parentNodes)
			    	 }
			         $($menuTree.selector).treeview('selectNode', [selectNodes,{silent: true}]);
			     }
			},
			onNodeUnselected: function (event, node) {
				 var selectNodes = TreeKit.getChilrdNode($menuTree.selector,node);//获取所有子节点
			     if(selectNodes){ //子节点不为空，则取消选中所有子节点
			         $($menuTree.selector).treeview('unselectNode', [selectNodes,{silent: true}]);
			         }
			         TreeKit.parentUnselect($menuTree.selector,node);
			}
		}
		if(options==undefined){
			options={};
		}
		$.extend(options,defaultOptions);
		var $menuTree = $(domId).treeview(options);
		return $menuTree;
	},
	//递归获取所有的子结点id
	getChilrdNode: function(tree,node,selected,type){
	        var ts = [];
	        if(node.nodes){
	            for(var x in node.nodes){
	            	if(selected==undefined || (selected!=undefined && node.nodes[x].state.selected==selected)){
	            		if(type!=undefined){
	           				ts.push(node.nodes[x][type])
	           			}else{
	           				ts.push(node.nodes[x].nodeId)
	           			}
	           		}
	                if(node.nodes[x].nodes){
	                var tempNodes = TreeKit.getChilrdNode(tree,node.nodes[x],selected,type);
	                	$.merge(ts,tempNodes);
	                }
	            }
	        }else{
	        	if(selected==undefined || (selected!=undefined && node.state.selected==selected)){
	        		if(type!=undefined){
           				ts.push(node[type])
           			}else{
           				ts.push(node.nodeId);
           			}
	       		}
	       }
	   return ts;
	},
	getParentNode: function(tree,node,selected,type){
	    var ts = [];
	    if(node.parentId!=undefined){
	    	var tempNodes = [];
	    	if(selected==undefined || (selected!=undefined && node.state.selected==selected)){
	    		if(type!=undefined){
       				ts.push(node[type])
       			}else{
       				ts.push(node.nodeId);
       			}
	   		}
			tempNodes = TreeKit.getParentNode(tree,$(tree).treeview('getNode', node.parentId),selected,type);
			$.merge(ts,tempNodes);
		}else{
			if(selected==undefined || (selected!=undefined && node.state.selected==selected)){
				if(type!=undefined){
       				ts.push(node[type])
       			}else{
       				ts.push(node.nodeId);
       			}
	   		}
		}
		return ts;
	},
	parentUnselect: function(tree,node){
		if(node.parentId!=undefined){
	  		var parentNode = $(tree).treeview('getNode', node.parentId);
	  		var chilrdenNodes = TreeKit.getChilrdNode(tree,parentNode,true);
	  		if(chilrdenNodes.length==0){
	  			$(tree).treeview('unselectNode', [node.parentId,{silent: true}])
	  		}
	  		TreeKit.parentUnselect(tree,$(tree).treeview('getNode', node.parentId))
	  	 }
	}
}