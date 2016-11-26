#一刻足球相关封装工具使用说明


##视图状态模块
### ParentView类
####简介
继承于`FrameLayout`内有四种状态,为所有页面的基本状态，每种状态会显示不同的样式， 内容错误 ，内容为空状态下自带下拉刷新功能。
```
  //状态枚举  正在加载中  普通  内容错误 ，内容为空
    public enum Staus {
        Loading, Normal, Error, Null
    }
```
**内容错误**
![内容错误](http://p1.bpimg.com/567571/19f4a7c6dd3c4b51.png)

**加载中**
![加载中](http://p1.bpimg.com/567571/43f2ee3d21e5983e.png)

**内容为空**
![内容为空](http://p1.bpimg.com/567571/9777b960c9b669c9.png)


相关xml属性
```
  <declare-styleable name="ParentView">
         <!--是否拦截事件-->
        <attr name="InterceptTouch" format="boolean"/>
           <!--是否可以滑动-->
        <attr name="canScroll" format="boolean"/>
          <!--历史遗留属性-->
        <attr name="content_not_scroll" format="boolean"/>
          <!--方便在xml布局里查看-->
        <attr name="is_debug" format="boolean"/>
         <!--是否显示进度-->
        <attr name="show_progress" format="boolean"/>
    </declare-styleable>
```

------------------------------

####使用方法
1. 设置当前页面状态 默认状态为加载中
 int... flag为遗留问题，其实没有多少作用，传递了则立即改变视图状态，否则会有一个淡入淡出动画。
 staus 为上述枚举变量
```
 public void setstaus(Staus staus, int... flag)；
```
------------------------------

2. 设置刷新回调
会在数据为空和数据错误的情况下刷新回调
```
 public void setReFreshDataListener(ReFreshDataListener reFreshDataListener);
```
3. 设置加载中、数据错误、数据为空状态下的提示文字
```
//数据为错误文字
public void setError_text(String error_text);
//设置数据为空文字
public void setNull_text(String null_text)
//加载中文字,多个文字随机轮播
public void setLoadding_list(List<String> loadding_list)
```
4. 进度条控制
加载中视图可以显示进度。相关函数如下
```
//可在xml中设置
app:is_show_progress
```
设置进度
```
//带变化的动画
public void setProgress(int progress) 

//不带变化的动画
public void setProgressNoAnim(int progress)
```

### RefreshScrollParentViewBase衍生类
RefreshScrollParentViewBase继承于ParentView类另外增加了下拉刷新功能与分页加载。
####衍生类简介
    1. ReFreshListViewParentView 用于ListView 的下拉刷新和上拉加载
    2. RefreshScrollParentView  用于scrollView 的下拉刷新
    
####内容简介

1. 一般属性
```
    //设置下拉刷新回调
     public void setOnCenterReFreshListener(RefreshScrollParentViewBase.onCenterReFreshListener onCenterReFreshListener)
     
     //设置加载更多回调
     public void setLoaddingMoreListener(LoaddingMoreListener loaddingMoreListener)
     
     //分页加载完调用
      public void loaddingMoreComplete(boolean isFinish)
     
     //下拉刷新加载完调用
    public void completeLoad(boolean succeed) 
      
```
 2. 特殊属性
    xml中 `app:postion` 当使用 RefreshScrollParentView的时候 ScrollView 的第一个子ViewGroup 中需要刷新位置上方为一个`LinearLayout` postion 可指定刷新的位置。
```
  reFreshBottom = (LinearLayout) scrollChildView.getChildAt(refresh_position);
```
设置postion的缘由如上。

####使用方法
使用嵌套的方式。
ListView
```
  <com.yioks.lzclib.View.ReFreshListViewParentView
        android:id="@id/parent_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ListView
            android:id="@id/listview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </com.yioks.lzclib.View.ReFreshListViewParentView>
    
```
ScrollView   
```
<com.yioks.lzclib.View.RefreshScrollParentView
        android:id="@+id/parent_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:postion="1">

        <ScrollView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:dragOverScrollHeadEnable_sv="false">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical">
                .....
                ....
        </ScrollView>
</com.yioks.lzclib.View.RefreshScrollParentView>        
```

###惯性滑动&弹性视图
####OverScrollScrollView类&
使用同继承自ScrollView,ListVIew，相关属性如下。
```
<declare-styleable name="OverScrollScrollView">
        <!--惯性滑动开关-->
        <attr name="flingOverScrollEnable_sv" format="boolean"></attr>
        
          <!--弹性拖动开关-->
        <attr name="dragOverScrollEnable_sv" format="boolean"></attr>
        
         <!--头部弹性拖动开关-->
        <attr name="dragOverScrollHeadEnable_sv" format="boolean"></attr>
        
        <!--底部弹性开关-->
        <attr name="dragOverScrollFootEnable_sv" format="boolean"></attr>
</declare-styleable>

```

##数据请求模块
###组成

#### Bean

Bean类
所有从网络获取的数据类的父类
```
public class Bean implements Serializable {
    public JsonManager jsonManager;

    public JsonManager getJsonManager() {
        return jsonManager;
    }

    public void setJsonManager(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }
}
```

JsonManger类
为所有接口的外层返回值
```
public class JsonManager implements Serializable{
    String code="";//获取数据
    String msg;//消息
    int time;//时间戳
    String dataInfo;//数据内容
    String flag="";//请求标识
    String dataKey;//数据MD5标识
    String codeKey;//指纹校验key

   //get set 方法

   //解析方法
    }
```

-------------- 

####网络请求接口

让Bean类为其实现类，重写相应方法。`SetParams()`为传参方法，`resolveDataXXX`为数据解析方法，在里面解析Json数据
    
    ```
    //基础接口
    public interface RequestDataBase {
    RequestParams SetParams(RequestParams requestParams,int type,String... strings) throws           Exception;
    }
    ```
    
    
    ```
    //返回值为Bean类型
    public interface RequestData extends RequestDataBase{
    Object resolveData(Object data) throws Exception;
    }
    ```
    
    
    ```
    //返回值为BeanList类型
    public interface RequestDataByList extends RequestDataBase {
    Object resolveDataByList(Object data) throws Exception;
    }
    ```
    
    ```
    //请求值带File类型
    public interface RequestDataFile{
    public RequestParams setFileParams(RequestParams requestParams, File[] files, String... strings) throws Exception;
}
    ```
    
   
-------------- 
    
####网络请求工具类
    ResolveDataHelper为网络请求类为抽象函数，需实现
``` 
public abstract boolean checkTokenError();
public abstract void tokenError();
```
其构造函数 RequestDataBase 为上述接口， RequestParams 为底层网络请求框架器的参数类
```
  public ResolveDateHelperImp(Context context, RequestDataBase requestData) {
        super(context, requestData);
    }

    public ResolveDateHelperImp(Context context, RequestDataBase requestData, RequestParams requestParams) {
        super(context, requestData, requestParams);
    }
    
```

--------------

`ParamsBuilder`简介用于方便构建请求参数的外层值，生成RequestParams对象

```
public class ParamsBuilder {
    private String typeId = "";
    private String version = "1";
    private String data_md5 = "";
    private String key = "";
    private String flag = "";
    private String method = "";
    private RequestParams params;
    private Context context;

    public ParamsBuilder(Context context) {
        this.context = context;
    }

    //省略set get方法

    public RequestParams build() {
        params = new RequestParams();
        String time = System.currentTimeMillis() + "";//时间戳
        params.put("a", method.trim());//操作
        params.put("t", typeId);//类型ID
        params.put("v", version);//版本
        if(context instanceof Activity)
        {
            params.put("mpKey", DeviceUtil.getDeviceUUID((Activity) context));//手机序列号
        }
        else
        {
            params.put("mpKey","");
        }

        params.put("_t", time);//时间戳
        params.put("dataKey", data_md5);//数据的MD5标识
        params.put("codeKey", key);//指纹校验KEY
        params.put("flag", flag);
        return params;
    }
```

--------------

网络请求回调 
成功返回与失败返回
1. 成功返回与RequestDate 对应的Bean 类或 `List<Bean>`
2. 失败返回失败错误

```
public interface onResolveDataFinish {

    /**
     * @exception NullPointerException
     */
    void resolveFinish(Object data);

    /**
     * @exception NullPointerException
     * @param code
     */
    void onFail(String code);
}
```


--------------

综合
###使用方法如下
```
ResolveDataHelper resolveDataHelper = new ResolveDateHelperLib(context, new Match(), new ParamsBuilder(context).setMethod("match_getMatchOfList ").build());
        //设置请求方式
        // 0 请求Bean 1 请求 List  
        resolveDataHelper.setDateType(1);
        //设置请求标签用于取消请求
        resolveDataHelper.setTAG('tag');
        //设置请求地址 ,有默认值 可从 GlobalVariable.HTTP 设置
        resolveDataHelper.setRequestHTTP("");
        //设置请求方式 0 post 1 get
        resolveDataHelper.setRequestType(1)
        
        //设置进度回调
        resolveDataHelper.setOnProgresUpDate(....);
        //设置请求回调
        resolveDataHelper.setOnResolveDataFinish(new onResolveDataFinish() {
            @Override
            public void resolveFinish(Object o) {
                //成功之后处理
                onSuccessDo(o);
            }

            @Override
            public void onFail(String s) {
              //失败之后处理
                onFailDeal();
            }
        });
        //请求参数，变参
        resolveDataHelper.StartGetData("","","");
```


##图片选择&拍照&裁剪模块
###TakePhoteBaseActivity类
需要选择图片的页面继承之，调用`showPopwindow()`无需额外操作，会自动调用相关功能
```
//调用后弹出窗口 bili为宽高比例，1为正方形 -1为不裁剪，用于裁剪 。limitCount控制选择图片的张数（只有选择图片适用）
 public PopupWindow showPopwindow(Activity activity, float bili, int limitCount)
```

```
    //裁剪后图片回调
   public abstract void onCutPicfinish(File file);

    //未裁剪的图片回调
    public abstract void onCutPicfinish(Uri uri);

    //多张图片回调
    public abstract void onCutPicfinish(Uri[] uris);
```
###PickImgActivity类
选择相片页面，请求方式
```
        Intent intent = new Intent();
        intent.setClass(TakePhoteBaseActivity.this, PickImgActivity.class);
        intent.putExtra("limitsize", limitsize);
        startActivityForResult(intent, PickImgActivity.PICK_MANY_PIC);
```
在 获取返回值
```
        if (requestCode == PickImgActivity.PICK_MANY_PIC) {
            if (data == null) {
                return;
            }
            Parcelable[] parcelables = data.getParcelableArrayExtra("uriList");
            Uri[] uris = new Uri[parcelables.length];
            for (int i = 0; i < parcelables.length; i++) {
                uris[i] = (Uri) parcelables[i];
            }
        }
```

###PicCultActivity类
请求方式
```
        Intent intent = new Intent();
        intent.setClass(this, PicCultActivity.class);
        intent.setData(uri);
        intent.putExtra("bili", bili);//设置裁剪比例
        startActivityForResult(intent, PicCultActivity.CULT_PIC);
```

返回值
```
        if (requestCode == PicCultActivity.CULT_PIC) {
            if (data != null) {
                String filepath = data.getStringExtra("filepath");
                if (filepath == null) {
                    return;
                } else {
                    File file = new File(filepath);
                }

            }
            return;
        }
```


##提示模块
###Dialog
构造函数
```
// 参数依次为 上下文  dialog内容  标题（可为空） 能否触摸屏幕外取消 能否点返回键取消 
//是否只有确定
     public MyDialog(Context context, String message, @Nullable String title, boolean canTouchCancel, boolean canBackCancel, boolean isConfirm)
```
设置回调
```
//确定回调
public void setOk_button_click_listener(View.OnClickListener ok_button_click_listener)

//取消回调
public void setCancel_button_click_listener(View.OnClickListener cancel_button_click_listener)
```
显示与取消
```
 public void showDialog()
 public void dismissDialog()
```


###Toast
处理了Toast相同重复弹出的问题
```
  DialogUtil
  public static void ShowToast(Context context, String str)
```

###等待Dialog
用户等待时弹出的dialog
```
    DialogUtil
   public static void showDialog(Context context, String content)
```

##常用封装Activity

####TitleBaseActivity
封装了标题栏，并修改了状态栏的颜色。
使用方式 继承之
```
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);
        //初始化状态栏
        setTitleState();
        //设置标题栏
         bindTitle(true, "比赛列表", -1);

    }
```

```
//leftRes 是否显示返回按钮,点击事件已写
//titleString 标题文字 
//rightRes 右边图标|右边文字
 public void bindTitle(boolean leftRes, String titleString, int rightRes)
 public void bindTitle(boolean leftRes, String titleString, String rightRes)
```

xml 可以引入标题
```
 <include layout="@layout/include_title_layout" />
```

####ReceiverTitleBaseActivity
注册了一个广播，用于在其他页面刷新本页
```
 public static void CallReFresh(Context context,Class class_name)
```


####RefreshListActivity
封装了一般列表页的业务逻辑,需要列表所对应的数据类如BeanImp
需配合ListAdapter使用
```
public abstract class ListAdapter <T extends Bean> extends BaseAdapter
{
    public List<T> list =new ArrayList<>();
    public Context context;
    ..............
    ..............
}
```
使用方式，继承RefreshListActivity,
```
public class MyActivity extends RefreshListActivity<BeanImp>
```
实现下面的方法
```
    //在其中请求数据
    public abstract void GetData();

    //返回ListView的Adapter
    public abstract ListAdapter getAdapter();

    //点击事件回调
    public abstract void onClick(int id);
```

在oncreate里调用
```
private
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myLayout);
        setTitleState();
        initView();
        bindTitle(.....);
        isMore = false;
        GetData();
    }
```

myLayout xml 需要以下元素 OverScrollListView可换为任意ListView，**id务必保持一致**

```
 <include layout="@layout/include_title_layout" />
 <com.yioks.lzclib.View.ReFreshListViewParentView
        android:id="@id/parent_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.yioks.lzclib.View.OverScrollListView
            android:id="@id/listview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:divider="@color/line_color"
            android:dividerHeight="5dp"
            app:dragOverScrollEnable_lv="false" />

    </com.yioks.lzclib.View.ReFreshListViewParentView>
```

####WebActivity
    封装的webView 浏览器。使用方式
    ```
         Intent intent = new Intent();
        intent.setClass(context, WebActivity.class);
        //访问的Url
        intent.putExtra("url", "----");
        //访问的标题
        intent.putExtra("title", "----");
        startActivity(intent);
    ```