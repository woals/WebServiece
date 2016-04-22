package com.yinyxn.webserviece;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {

    private EditText phoneSecEditText;
    private TextView resultView;
    private Button queryButton;
    String result;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0x123) {
                resultView.setText(result);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneSecEditText = (EditText) findViewById(R.id.phone_sec);
        resultView = (TextView) findViewById(R.id.result_text);
        queryButton = (Button) findViewById(R.id.query_btn);
        queryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 手机号码（段）
                final String phoneSec = phoneSecEditText.getText().toString().trim();
                // 简单判断用户输入的手机号码（段）是否合法
                if ("".equals(phoneSec) || phoneSec.length() < 7) {
                    // 给出错误提示
                    phoneSecEditText.setError("您输入的手机号码（段）有误！");
                    phoneSecEditText.requestFocus();
                    // 将显示查询结果的TextView清空
                    resultView.setText("");
                    return;
                }

                // 查询手机号码（段）信息
                new Thread() {
                    public void run() {
                        getRemoteInfo(phoneSec);
                    }
                }.start();
            }
        });
    }

    /**
     * 手机号段归属地查询
     *
     * @param phoneSec 手机号段
     */
    public void getRemoteInfo(String phoneSec) {

        // 命名空间
        String nameSpace = "http://WebXml.com.cn/";
        // 调用的方法名称
        String methodName = "getMobileCodeInfo";
        // EndPoint
        String endPoint = " http://ws.webxml.com.cn/WebServices/MobileCodeWS.asmx";
        // SOAP Action
        String soapAction = "http://WebXml.com.cn/getMobileCodeInfo";
        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的两个参数mobileCode、userId
        rpc.addProperty("mobileCode", phoneSec);
        rpc.addProperty("userId", "");

        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);
        HttpTransportSE transport = new HttpTransportSE(endPoint);
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取返回的数据
        SoapObject object = (SoapObject) envelope.bodyIn;
        // 获取返回的结果
        result = object.getProperty(0).toString();
        // 将WebService返回的结果显示在TextView中
        //resultView.setText(result);
        // Message message = new Message();
        handler.sendEmptyMessage(0x123);
    }
}
