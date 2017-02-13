package com.example.dinhtho.detectsms_call_notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    static IntentFilter phoneFilter;
    static IntentFilter smsFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        phoneFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mPhoneCallReceiver, phoneFilter);

        smsFilter = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        smsFilter.setPriority(2147483647);
        registerReceiver(mSmsReceiver, smsFilter);


    }
    private final BroadcastReceiver mPhoneCallReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                TelephonyManager manager = (TelephonyManager) context
                        .getSystemService(context.TELEPHONY_SERVICE);
                Log.i(TAG, "onReceive: "+"co dien thoai");
            }
        }
    };
    private final BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Receive sms");
            processReceive(context, intent);

//            if (isSMSEnable()) {
//                sendSMSColor();
//            }
        }
        public void processReceive(Context context, Intent intent)
        {
            Toast.makeText(context, "Hello .. có tin nhắn tới đó", Toast.LENGTH_LONG).show();
            TextView lbl=(TextView) findViewById(R.id.smsBody);
            //pdus để lấy gói tin nhắn
            String sms_extra="pdus";
            Bundle bundle=intent.getExtras();
            //bundle trả về tập các tin nhắn gửi về cùng lúc
            Object []objArr= (Object[]) bundle.get(sms_extra);
            String sms="";
            //duyệt vòng lặp để đọc từng tin nhắn
            for(int i=0;i<objArr.length;i++)
            {
                //lệnh chuyển đổi về tin nhắn createFromPdu
                SmsMessage smsMsg=SmsMessage.
                        createFromPdu((byte[]) objArr[i]);
                //lấy nội dung tin nhắn
                String body=smsMsg.getMessageBody();
                //lấy số điện thoại tin nhắn
                String address=smsMsg.getDisplayOriginatingAddress();
                sms+=address+":\n"+body+"\n";
            }
            //hiển thị lên giao diện
            lbl.setText(sms);
        }
    };
}
