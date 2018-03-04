package code.loginandregistration.com.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet() {
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                //Toast.makeText(_context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();

                return true;

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                //connected to Data
                //Toast.makeText(_context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();

                return true;
            }
        } else {
            // not connected to the internet
        }
        return false;
    }
}