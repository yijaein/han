package kr.co.multi.beacontest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class CompassView extends View{
    //나침반 이미지
    Bitmap compassImg;
    //화면의 정중앙의 좌표
    int centerX, centerY;
    //나침반의 폭과 높이의 반지름(절반)
    int compassX, compassY;
    public CompassView(Context context) {
        super(context);
        //화면의 폭과 높이를 얻어오기 위해
        WindowManager managerW = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = managerW.getDefaultDisplay();
        //화면의 폭과 높이 얻어오기
        int screenW = 210; //display.getWidth();
        //getHeight하면 위에 상태바를 포함한 화면 전체의 길이를 반환한다.
        int screenH = 210; //display.getHeight();
        //중심의 좌표
        centerX = screenW/2;
        centerY = screenH/2;
        //리소스에 등록된 이미지 읽어오기
        compassImg=BitmapFactory.decodeResource(getResources(), R.drawable.compass);
        //이미지 크기 조정하기
        compassImg = Bitmap.createScaledBitmap(compassImg, screenW, screenW, false);
        //크기를 조정한 이미지의 크기 얻어오기
        compassX = compassImg.getWidth()/2;
        compassY = compassImg.getHeight()/2;

        //핸들러에 메세지 보내서 핸들러가 무한 루프가 돌도록 한다.
        handler.sendEmptyMessage(0);
    }
    //화면 그리는 메소드
    @Override
    protected void onDraw(Canvas canvas) {
        //캔바스 회전시키기(시계방향으로 회전시킬 360분법의 각, 회전축x, 회전축y)
        canvas.rotate( -heading, centerX, centerY);
        canvas.drawBitmap(compassImg, (centerX-compassX), (centerY-compassY), null);
    }

    //화면을 갱신하는 메소드
    int heading;
    public void updateUI(int heading){
        this.heading = heading;
    }
    //화면을 주기적으로 갱신하기 위한 Handler 객체(*자주쓰임)
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            // 화면을 갱신한다
            invalidate();
            // 10/1000초마다 자신의 객체에 다시 메세지를 보내서 무한 루프가 되도록 한다.
            handler.sendEmptyMessageDelayed(0, 10);

        }
    };

}