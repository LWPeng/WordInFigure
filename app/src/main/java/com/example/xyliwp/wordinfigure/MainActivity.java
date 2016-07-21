package com.example.xyliwp.wordinfigure;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xyliwp.wordinfigure.activity.BaseActivity;
import com.example.xyliwp.wordinfigure.customcontrols.MainFragment;
import com.example.xyliwp.wordinfigure.customcontrols.PictureSavePopWindow;
import com.example.xyliwp.wordinfigure.popwindos.ContextMenuDialogFragment;
import com.example.xyliwp.wordinfigure.popwindos.MenuObject;
import com.example.xyliwp.wordinfigure.popwindos.MenuParams;
import com.example.xyliwp.wordinfigure.popwindos.interfaces.OnMenuItemClickListener;
import com.example.xyliwp.wordinfigure.popwindos.interfaces.OnMenuItemLongClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends BaseActivity implements OnMenuItemClickListener,
        OnMenuItemLongClickListener {

    private Button button;
    private ImageButton imageButton_menu;
    private ImageView imageView_main;
    private FileInputStream is = null;
    public static Context activity;
    public static Uri uriImageview;

    private FragmentManager fragmentManager;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private Bitmap bitmap_guiduhua;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        activity = this.getApplicationContext();
        //id绑定
        idBinding();
        //控件的点击事件的设置
        onClick();

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        addFragment(new MainFragment(), true, R.id.container);
    }

    /**
     * activity布局文件中id的绑定
     */
    private void idBinding() {
        button = (Button) findViewById(R.id.bitton);
        imageButton_menu = (ImageButton)findViewById(R.id.main_imagebutton_menu);
        imageView_main = (ImageView)findViewById(R.id.main_imageview_tupian);
    }

    /**
     * 控件的点击事件设置
     * 利用匿名类来注册监听事件
     */
    private void onClick() {
        //黑白照的按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_main.setImageBitmap(bitmap_guiduhua);
            }
        });

        //图片保存的点击事件
        imageView_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PictureSavePopWindow pictureSavePopWindow = new
                        PictureSavePopWindow(MainActivity.this,"12");
                pictureSavePopWindow.showAsDropDown(imageView_main);
                return false;
            }
        });

        //弹出窗口的点击事件
        imageButton_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("menu点击----->","menu");
//                MainMenuPopWindow mainMenuPopWindow = new MainMenuPopWindow(MainActivity.this);
//                mainMenuPopWindow.showPopupWindow(imageButton_menu);
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("requestCode---->", ""+requestCode);
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    /**
                     * 判断手机版本，因为在4.4版本都手机处理图片返回的方法就不一样了
                     * 4.4以后返回的不是真实的uti而是一个封装过后的uri 所以要对封装过后的uri进行解析
                     */

                    if (Build.VERSION.SDK_INT >=19){
                        //4.4系统一上用该方法解析返回图片
                        handleImageOnKitKat(data);
                    }else{
                        //4.4一下用该方法解析图片的获取
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            case 2:
                Log.e("case2---->", "22222222222222222222");
                if (resultCode == RESULT_OK){
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uriImageview,"image/*");
                    intent.putExtra("scale",true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,uriImageview);
                    startActivityForResult(intent,3);//启动裁剪程序
                }
                break;
            case 3:
                if (resultCode == RESULT_OK){
                    try{
                        Log.e("case3---->", "3333333333333");
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                        .openInputStream(uriImageview));
                        //将裁剪后的图片显示出来
                        imageView_main.setImageBitmap(bitmap);
                        bitmap_guiduhua.recycle();
                        bitmap_guiduhua = huiDuHua(bitmap);
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
        }


    }

    /**
     * api 19以后
     *  4.4版本后 调用系统相机返回的不在是真实的uri 而是经过封装过后的uri，
     * 所以要对其记性数据解析，然后在调用displayImage方法尽心显示
     * @param data
     */

    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的uri 则通过id进行解析处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                //解析出数字格式id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" +id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                        "content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equals(uri.getScheme())){
            //如果不是document类型的uri，则使用普通的方式处理
            imagePath = getImagePath(uri,null);
        }
        displayImage(imagePath);
    }

    /**
     * 4.4版本一下 直接获取uri进行图片处理
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    /**
     * 通过 uri seletion选择来获取图片的真实uri
     * @param uri
     * @param seletion
     * @return
     */
    private String getImagePath(Uri uri, String seletion){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,seletion,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 通过imagepath来绘制immageview图像
     * @param imagPath
     */
    private void displayImage(String imagPath){
        if (imagPath != null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagPath);
            imageView_main.setImageBitmap(bitmap);
            bitmap_guiduhua = huiDuHua(bitmap);
            //bitmap.recycle();
        }else{
            Toast.makeText(this,"图片获取失败",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 对图像进二值化处理
      * @param graymap
     * @return
     */
    public Bitmap huiDuHua(Bitmap graymap) {
        //得到图形的宽度和长度
        int width = graymap.getWidth();
        int height = graymap.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);

                // 新的ARGB
                int newColor = colorToRGB(255,gray,gray,gray);
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

    /**
     * 图片灰度像素转化过程
     * @param alpha
     * @param red
     * @param green
     * @param blue
     * @return
     */
    private int colorToRGB(int alpha, int red, int green, int blue) {

        /************************************************************
         * 对于正数和负数在不考虑溢出的情况下，他们的运算如下：			*
         * 3 << 2，则是将数字3左移2位，解释如下：						*
         * 左移一位都相当于乘以2的1次方，左移n位就相当于乘以2的n次方。	*
         * 即  newPixel << 8  即等于 newPixel * 2^8						*
         * **********************************************************
         */
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;

    }

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);
        mMenuDialogFragment.setItemLongClickListener(this);
    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.mipmap.quxiao);

        MenuObject send = new MenuObject("从相册中选择");
        send.setResource(R.mipmap.xiangce);

        MenuObject like = new MenuObject("拍照");
        like.setResource(R.mipmap.paizhao);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        return menuObjects;
    }


    protected void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.meu, menu);
        return true;
    }


    @Override
    public void onMenuItemClick(View clickedView, int position) {
        switch (position){
            case 0:
                break;
            case 1:
                Log.e("xiangce---->","相冊");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                break;
            case 2:
                //以系统时间作为该文件 民命
                SimpleDateFormat formatter  =   new    SimpleDateFormat("yyyy年MM月dd日HH-mm-ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String    str    =    formatter.format(curDate);
                Log.e("拍照---->",str);
                //建立file文件用于保存来拍照后的图片
                File outputFile = new File(Environment.getExternalStorageDirectory(),str+".jpg");
                /**
                 * 使用隐式intent进行跳转
                 */
                try {
                    if (outputFile.exists()){
                        outputFile.delete();
                    }
                    outputFile.createNewFile();

                }catch (Exception e){
                    e.printStackTrace();
                }
                uriImageview = Uri.fromFile(outputFile);
                //另mainactivity的uri等于本.java 文件中的  uri
                Intent intent1 = new Intent("android.media.action.IMAGE_CAPTURE");
                intent1.putExtra(MediaStore.EXTRA_OUTPUT,uriImageview);
                //启动相机程序
                startActivityForResult(intent1,2);
                break;
        }
    }

    @Override
    public void onMenuItemLongClick(View clickedView, int position) {
        Toast.makeText(this, "Long clicked on position: " + position, Toast.LENGTH_SHORT).show();
    }

}
