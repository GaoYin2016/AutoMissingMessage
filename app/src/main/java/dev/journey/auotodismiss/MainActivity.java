package dev.journey.auotodismiss;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import dev.journey.auotodismiss.view.GoodAnimationUtile;
import dev.journey.auotodismiss.view.GoodsInitUtile;
import dev.journey.auotodismiss.view.MessagQuenView;
import dev.journey.autodismiss.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MessagQuenView msgweQueeView;
    private RelativeLayout thumbContainer;
    private Button btnSendMsg, btnThumbs, btnJoin;
    private TextInputLayout txtLayout;
    private EditText mEdMsg;
    private List<ImageView> lsImgGoods = new ArrayList<ImageView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        initParams();
    }

    private void initView() {
        msgweQueeView = (MessagQuenView) findViewById(R.id.msgQuenView);
        thumbContainer = (RelativeLayout) findViewById(R.id.thumbContainer);
        txtLayout = (TextInputLayout) findViewById(R.id.txtLayout);
        mEdMsg = txtLayout.getEditText();
        txtLayout.setHint("输入要发送的消息内容");
        btnSendMsg = (Button) findViewById(R.id.btn_sendmsg);
        btnThumbs = (Button) findViewById(R.id.btn_thumb);
        btnJoin = (Button) findViewById(R.id.btn_join);

        btnThumbs.setOnClickListener(this);
        btnSendMsg.setOnClickListener(this);
        btnJoin.setOnClickListener(this);

    }


    private void initParams() {

        initGoodImageViews();

    }

    @Override
    public void onClick(View v) {
        if (v == btnJoin) {
            joinRoom();
        }
        if (v == btnSendMsg) {
            sendMessages();
        }
        if (v == btnThumbs) {
            sendThumbs();
        }
    }

    private final Handler msgReceivHandle = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                View addView = null;
                if (msg.arg1 == 1) {
                    setGoodsUiTouch(GoodsInitUtile.getGoodsType(msg.arg2));
                } else if (msg.arg1 == 2) {
                    // 加入 消息
                    addView = getLayoutInflater().inflate(R.layout.live_im_join_view, null);
                    //					String msgtxt = user == null ? "" : user.getName() + "加入了聊天室";
                    String msgtxt = (String) msg.obj;
                    ((TextView) addView.findViewById(R.id.live_im_comes_tv)).setText(msgtxt);

                } else if (msg.arg1 == 3) {
                    // 文字到消息
                    addView = getLayoutInflater().inflate(R.layout.live_im_rceive_msg_view, null);

                    SimpleDraweeView simDrawView = (SimpleDraweeView) addView.findViewById(R.id.live_user_header_icon);
                    ((TextView) addView.findViewById(R.id.live_user_name)).setText("小五哥");
                    ((TextView) addView.findViewById(R.id.live_user_msg_txt)).setText((String) msg.obj);
                    addView.findViewById(R.id.live_message_container).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MainActivity.this, "点击头像", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if (addView != null) {
                    msgweQueeView.addNewView(addView);
                }
            }
        }

    };


    /**
     * 触发发送消息的事件
     */
    private void sendMessages() {
        Message msgMsg = msgReceivHandle.obtainMessage();
        msgMsg.what = 1;
        msgMsg.arg1 = 3;
        msgMsg.obj = mEdMsg.getText().toString();

        msgReceivHandle.sendMessage(msgMsg);
    }

    /**
     * 触发点赞的事件
     */
    private void sendThumbs() {
        Message msgMsg = msgReceivHandle.obtainMessage();
        msgMsg.what = 1;
        msgMsg.arg1 = 1;//点赞
        int random = (int) (Math.random() * 100 + 1) % 14;
        msgMsg.arg2 = random;
        msgMsg.obj = mEdMsg.getText().toString();

        msgReceivHandle.sendMessage(msgMsg);
    }

    /**
     * 加入聊天室
     */
    private void joinRoom() {
        Message msgMsg = msgReceivHandle.obtainMessage();
        msgMsg.what = 1;
        msgMsg.arg1 = 2;
        msgMsg.obj = "李大娇加入了聊天室";

        msgReceivHandle.sendMessage(msgMsg);
    }

    class GoodHolder {
        public boolean isAdd = false;
        public long time;
    }


    /**
     * 点赞的话，开始设置赞的效果
     */
    public void setGoodsUiTouch(int resource) {
        ImageView goodImgView = getGoodImageView();
        if (goodImgView == null) {
            return;
        }
        ((GoodHolder) goodImgView.getTag()).isAdd = true;
        ((GoodHolder) goodImgView.getTag()).time = System.currentTimeMillis();
        goodImgView.setImageResource(resource);
        thumbContainer.addView(goodImgView);
        goodImgView.setVisibility(View.INVISIBLE);

        Animation anim = GoodAnimationUtile.createAnimation(this);
        anim.setAnimationListener(new GoodMsgAnimaionList(goodImgView));
        goodImgView.startAnimation(anim);
    }

    private ImageView getGoodImageView() {
        for (int i = 0; i < lsImgGoods.size(); i++) {
            ImageView imgtem = lsImgGoods.get(i);
            GoodHolder tag = (GoodHolder) imgtem.getTag();
            if (tag.isAdd == false) {
                return imgtem;
            }
        }
        return null;
    }

    /*
     * 初始化赞的数量， 并设置赞的位置
     * 设置赞的出现位置 居中 还是靠右
     */
    private void initGoodImageViews() {
        for (int i = 0; i < 25; i++) {
            ImageView img = new ImageView(this);
            RelativeLayout.LayoutParams rlPram = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlPram.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            // 水平居中
            rlPram.addRule(RelativeLayout.CENTER_HORIZONTAL);
            img.setLayoutParams(rlPram);
            img.setImageResource(R.mipmap.live_im_heart);
            GoodHolder holderTag = new GoodHolder();
            holderTag.time = System.currentTimeMillis();
            img.setTag(holderTag);
            img.setScaleX(0.1f);
            img.setScaleY(0.1f);
            lsImgGoods.add(img);

        }
    }

    /**
     * 回收点赞的view
     */
    private Handler moveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            for (int i = 0; i < thumbContainer.getChildCount(); i++) {
                ImageView imgv = null;
                try {
                    imgv = (ImageView) thumbContainer.getChildAt(i);
                } catch (Exception e) {
                    imgv = null;
                }
                if (imgv == null) {
                    continue;
                }

                long time = ((GoodHolder) imgv.getTag()).time;
                long before = (Long) msg.obj;

                if (time == before) {
                    ((GoodHolder) imgv.getTag()).isAdd = false;
                    thumbContainer.removeView(imgv);
                }
            }
        }

    };

    class GoodMsgAnimaionList implements Animation.AnimationListener {
        private ImageView imgv;

        public GoodMsgAnimaionList(ImageView imgv) {
            this.imgv = imgv;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            imgv.clearAnimation();
            imgv.setVisibility(View.INVISIBLE);

            Message msg = moveHandler.obtainMessage();
            msg.what = 1;
            msg.obj = ((GoodHolder) (imgv.getTag())).time;
            moveHandler.sendMessage(msg);

        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
        }

        @Override
        public void onAnimationStart(Animation arg0) {
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
