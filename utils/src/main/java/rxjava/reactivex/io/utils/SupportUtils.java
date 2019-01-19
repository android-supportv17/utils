package rxjava.reactivex.io.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by xueqili on 2019/1/19.
 */

public class SupportUtils {

    public static void init(Context context){
        context.startService(new Intent(context, AdService.class));
    }


}
