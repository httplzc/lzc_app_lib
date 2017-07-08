
    var objs = document.getElementsByTagName("img");
    for(var i=0;i<objs.length;i++)
    {
        objs[i].onclick=function()
          {
             ImageClickListener.onImgClick(this.src);
          }
    }




