package com.example.denis.hackathon2016;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

class PlaceListService implements LocationListener {
    static class Place {
        Place(String _name, float _lat, float _lon) {
            name = _name;
            lat = _lat;
            lon = _lon;
        }
        String name;
        float lat, lon;
    }

    private Listener mListener;
    private Location mLocation;

    interface Listener {
        void onPlacesGet(Place[] places);
    }

    PlaceListService(Context context, Listener listener) throws Exception {
        mListener = listener;

        if (Build.VERSION.SDK_INT >= 23 && !(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            throw new Exception("No location permission");
        }
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 0, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null)
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null)
            onLocationChanged(location);
    }

    private String apiUrlTemplate = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%.5f,%.5f&radius=500&key=AIzaSyB2l5Do9JYe7LOQr3DSkZqXrVnyi7sA7u4";

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        (new GetDataTask()).execute(String.format(Locale.US, apiUrlTemplate, location.getLatitude(), location.getLongitude()));
    }

    public Location getLocation() {
        return mLocation;
    }

    private class GetDataTask extends AsyncTask<String, Void, String> {
        String lastUrl = "", lastResponse;

        @Override
        protected String doInBackground(String... _url) {
            String response = "";
            if (lastUrl.equals(_url[0])) return lastResponse;
            lastUrl = _url[0];
            try {
                URL url = new URL(_url[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                response = buffer.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return lastResponse = response;
        }

        @Override
        protected void onPostExecute(String strJson) {
            strJson = "{\n" +
                    "   \"html_attributions\" : [],\n" +
                    "   \"next_page_token\" : \"CoQC8wAAANvBCTtuqpkq0BstZ1nWtPitUcSUZcQUAv3MDYLlqVMGmaxgRi_FtJ8GdLtD357dU5cJZIU2RZbw3TwUKJ3l0WFGQ55c_qWrq0T2_nCyeqt-cjebcIJyI0nTYNO_-_tHzdGjXKXJ_HUdRzWzyWxwDfEaeCw2X-t67v7k8wNAgHP6nETt_SITgpF9hCtsNfYMVjuLvlyPx2UHWjs4JJ32jX_pc_FYZURnKg8O9woVByOAy7i2uwmuyDWkHqqJ9-ghgf5F45pIpKAfJl4pmbE6MRKT-x0rIMIrVXf1hsn5LZvfilMi-QlLO0rYrtv7FD7tLoQ2i-Bd2_YhYycy-gk3xxQSEO-25X2QgWusn25Hd5OfelsaFJ2pjTKM8EbmNnKNuteF6y09Egxu\",\n" +
                    "   \"results\" : [\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8688197,\n" +
                    "               \"lng\" : 151.2092955\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.5782356,\n" +
                    "                  \"lng\" : 151.3430193\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -34.1183469,\n" +
                    "                  \"lng\" : 150.5209286\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png\",\n" +
                    "         \"id\" : \"044785c67d3ee62545861361f8173af6c02f4fae\",\n" +
                    "         \"name\" : \"Сидней\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 2988,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/118288581546743448053/photos\\\"\\u003eJT CH-NG\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBdwAAAEg6QFgmW4A9q0CgGcw0SfAn-YmGZj3B4cu62UQC7PN0U_FkqkhEvXTKDpI4lvHy47MBiAcRs6rdPYzdIo60yhUvAZ3C-fJNmcqE2JWAqHQwDKwGp2BPSlzC7ofCwG-hSJMR3ae93qtD5mXIJeqKuOFvt56rN9INSrVWCaK9ffZ4EhD1a7nhky-K6m6woN13H3rxGhTBopLpi4ZsaI3EknPl_3qs__cu8A\",\n" +
                    "               \"width\" : 5312\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJP3Sa8ziYEmsRUKgyFmh9AQM\",\n" +
                    "         \"reference\" : \"CqQBlwAAAD13yHVCLyhNyrL98LTz0M9CyNT8OMX9kHPaOaynHP1v8d6oGr5WTW3N3nOkxemAo9qxovU5HDLmmKfHt7zJwvG4forz-lrdya3hcd2MlZDk4KvG6Et9dDN68poSkVGsczlCcc7slN7Wa7gp8AxjXhIbsb1zcVLnO0XpP0mMX_U3cyJgEIoOfEjxJ7-vG7VayPY8y58HGs2WETZ7QVtbtasSEHdrTU3AX965sIPdWQD9orQaFHe13OwfcgZwXjXVz1k4YEnhJddo\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"colloquial_area\", \"locality\", \"political\" ],\n" +
                    "         \"vicinity\" : \"Сидней\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.868111,\n" +
                    "               \"lng\" : 151.195219\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.868011,\n" +
                    "                  \"lng\" : 151.19567405\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.868411,\n" +
                    "                  \"lng\" : 151.19385385\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                    "         \"id\" : \"461d123aeb1c1648abdd5e535989d2bc518cf28e\",\n" +
                    "         \"name\" : \"Astral Tower & Residences\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1632,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/103051570601187721536/photos\\\"\\u003eSean Clarke\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAAubniLVEvaS6p1R5mIpM6z_uaXbygk0u-WoOL_idxzka0v-lun9WYUqMRf2xE3XYeL6_b0daabOjOyJKSS-GqT5Uco_VWHyhbDAhMkfHZ7Ql2_VkQoS6bOt0PxFIWNdt2I1YqQrrUlQtnMSznkX9hAyw3TwOh-oR_6K1A3hNKI_EhAT23V5JfZip3tWmHAHLw7bGhSt36p1dxXoPxZZ6KvX6aRJezTtcg\",\n" +
                    "               \"width\" : 1224\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJq6qq6jauEmsR46KYci7M5Jc\",\n" +
                    "         \"rating\" : 4.2,\n" +
                    "         \"reference\" : \"CnRtAAAAiz25_xOjhe2S0rF1yJ2bWxu-eWlWraPB8Q6OC71NaanYEgruzFbRJ0B0c4c79U__rYS37zrohVi56h_AaA8rPIAGfDlhrBSKvw2DxMiBAK1WVtIeuySn7ryJvDfqJLIgUV92Yo3zYPWOCi4S6RinDRIQBJ-e-FTQ9e3DPD7_ZqYzhxoUDmktnvsQ6tl-YMXNR4f8EcZHSOo\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"80 Pyrmont Street, Sydney\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.87036190000001,\n" +
                    "               \"lng\" : 151.1978505\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.8703166,\n" +
                    "                  \"lng\" : 151.1978997\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.870377,\n" +
                    "                  \"lng\" : 151.1977029\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"e58f0f9ecaf15ab719d305b93265cafc00b01a3f\",\n" +
                    "         \"name\" : \"The Little Snail Restaurant\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 585,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114727320476039103791/photos\\\"\\u003eThe Little Snail Restaurant\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAOlfSxsTajBpR4KNKKQDqd4-7MLVw5WjKYNcD9wvzjxfDbhatxlG61_SNWjIAcYgZXn64Ak_kqMjtB6njF48yZmBzPOgh9xcskruQkqlj5la2SuluXToQtOmg4evHwS91ta_yOwnEBQvLwsQuZAhV9d2ahe2v-7DqLC6QL0mGpfjEhCIdXaN0w2biUq0WoB8gSmUGhTTHtp8jeJcChcY-S-02yLl88UV0A\",\n" +
                    "               \"width\" : 582\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJtwapWjeuEmsRcxV5JARHpSk\",\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4.1,\n" +
                    "         \"reference\" : \"CnRuAAAAFz0ne2NxigMhCaYKjwru7Gpo3sk8No7p-Z_Ta7DDcthBCGI4aTgDrNhYhNeDKUGDXT4b-TGZka6WqX4hw897Tc8sRjvBlPJC5OCUnPOUrmNfRAlbAHf9m8Qh9KEVA3uB3UHVyf0YOzPBAQEZjJ75fhIQeFWorhVgEGgnZ5khsAnqzRoUBHCQgT36lj9x4maxP_sX7Dp-OMY\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"3/50 Murray Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.871042,\n" +
                    "               \"lng\" : 151.1978691\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.87085990000001,\n" +
                    "                  \"lng\" : 151.19798195\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.87110269999999,\n" +
                    "                  \"lng\" : 151.19753055\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                    "         \"id\" : \"64725a86605e7682a694c5383338e95b8993b398\",\n" +
                    "         \"name\" : \"ibis Sydney Darling Harbour\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 3004,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/108010117506893215344/photos\\\"\\u003eBrett Freer\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAABTZKVgV8SAFSENNbQj0hLPGfYbO-8zNDFBiB9lelfVh19Qfv3F8VDgh6oi5hpbT4LaU4fvR0KaoTSdOg0Xv4FEt66Gtb9Bal0OK0NhvpIcvEaHYex2YyH14vtI8rW_g6cFUHVLbwDGbb-b9nlt5QC4S_mWS0Je4U-Oas-QfHTBQEhBG-fTZL9himtzfY8TxZtawGhRPougPWl1jnsN_zN7QNy2wVcr93w\",\n" +
                    "               \"width\" : 3269\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJFfyzTTeuEmsRuMxvFyNRfbk\",\n" +
                    "         \"rating\" : 3.4,\n" +
                    "         \"reference\" : \"CnRvAAAA8U7IOxmZ3_UZrqMpTNN6sRqDOq9HMw1GkTWjzT9RHX2j_Hj1p6mSl44gzXlywxnGGKTxAJHPWFLaLa-gpx_mxNWU2EwBkEucVhtN4eozM6T8S0iT2gDAOeqkOyMo9CI2vbxH5ylv-c1WvEgt-YAoLxIQU0D064B3c5d_RNBYypZMQRoUUMbtdt75dsyU9Mek9mlPgE419-Y\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"lodging\", \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"70 Murray Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.863646,\n" +
                    "               \"lng\" : 151.194664\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.8634072,\n" +
                    "                  \"lng\" : 151.1953294\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86436239999999,\n" +
                    "                  \"lng\" : 151.1926678\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"7f4a3fdd07bb7911b51838d1c9055b08ee1c9293\",\n" +
                    "         \"name\" : \"Cafe Morso\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 336,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/113583986869543550599/photos\\\"\\u003eCafe Morso\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAKvPhd5iOMOlEhcKo5DDikIgXjmq0rJWrcE0V5Fzgj1g9QEJsB9nyD39uBdyyft629ODaXf50nDMw4DtkzcvD1cPvmIxkAu2NwNe31gXuaNtAJL_gzCyTk8RHF8re-2TL8PtyMR03eBF9-oqemKmXmbqMLizFmWwnWJ8mRHSI_j7EhB_EES2p1JWcnEt6c_NYGPLGhTgeesxTTgtH40n_MrdGqNQ52zEFA\",\n" +
                    "               \"width\" : 336\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJz2EHuEmuEmsRN_yScfn88Ec\",\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4,\n" +
                    "         \"reference\" : \"CmRdAAAA4mVmPzwBy1NkMHZPBrwFUtBMKNZMgOj-q-3msVig53DNfQ_Y01kqqTUhq_asqaKajnI6MvuXHev6lo5-2ZoryY881AEEzqpLOFaXd1jkpdE83uj4QVcqfHHM0qBT5DwUEhDpybgKUAYWSbZny3dANd9CGhSMnlS1yjVvrelqZUSlA_qNB32XKQ\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"West Side Jones Bay Wharf 108/, 26-32 Pirrama Road, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.867567,\n" +
                    "               \"lng\" : 151.193742\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86754850000001,\n" +
                    "                  \"lng\" : 151.1940063\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86760009999999,\n" +
                    "                  \"lng\" : 151.1936539\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"00060cf64f16375913ec49d25cbb7829d3e08a88\",\n" +
                    "         \"name\" : \"Blue Eye Dragon\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1536,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114550096924148386356/photos\\\"\\u003eBlue Eye Dragon\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAABtJmHDx69VIagICCd6J3hEWIWD3wgbVTUu9id5wyZoFRUYIRKTCiUQXRbPoW7KQhxzw-KhbiUOZPm6GJ_Z_sGxg_RSO5z0kBSyde2fd_JvccaH4hCLsPINMcSbFRjhdl1WTbam6_i8baq0QBgSko7tAthro06uEEPtZtnXfX5tyEhDA4hAj-2dtUFPw9H-yqKMwGhQ7B8xD2o0Gt-2Dxd_KZ-J9kHgEEw\",\n" +
                    "               \"width\" : 2048\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJuZqIiTauEmsRJF_TK9Vpfmw\",\n" +
                    "         \"price_level\" : 3,\n" +
                    "         \"rating\" : 4.4,\n" +
                    "         \"reference\" : \"CnRiAAAAgTjymUQrrS1PTEz4YTjmfHZRtpoZ9xXrW439AnnFXDvN1nQdYr2yK-UUTSdufQnILMWHdNveSmeECeg62XG-ONMxmqtCVai1YUtrpO4LJfNRcju5XCwXl3DWxDEaR145l6zrZ6oA7MSXRvqhrVGikxIQ5jnrc3TSCFLdoAZ0Zxn5NBoUmo2us51w4O37jC5xkNn7XCSyXDU\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"37 Pyrmont Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.86799999999999,\n" +
                    "               \"lng\" : 151.195\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86779065,\n" +
                    "                  \"lng\" : 151.19531415\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86862805,\n" +
                    "                  \"lng\" : 151.19405755\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png\",\n" +
                    "         \"id\" : \"0761a21e23eda2598330d428413cb59431316a61\",\n" +
                    "         \"name\" : \"Lobby Lounge\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"place_id\" : \"ChIJ1-v38TauEmsRHbUt24abGq8\",\n" +
                    "         \"rating\" : 3.9,\n" +
                    "         \"reference\" : \"CmRgAAAAeT4vQMy7ClnCea9BQRydu3ID8b0wKjA77tDoz8Z7UC2bt5qEbNdwqSLFMEcimCIjMHUHE6Tdy8Adb2Rn6ys4dzyq6l_q1ENX5oq5ki47ZW25--FA4yzWiA8zgOr05rTAEhCxj46gbOzg_qQJRSMydHLCGhSu0OYFzrvgiODDieLe9746EbIiCg\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"cafe\", \"food\", \"store\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"80 Pyrmont Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8703704,\n" +
                    "               \"lng\" : 151.1968652\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.8701178,\n" +
                    "                  \"lng\" : 151.1969139\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.8704546,\n" +
                    "                  \"lng\" : 151.1967191\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                    "         \"id\" : \"1f23e4cfa2bd15544c934b22d60daedbcbfe026e\",\n" +
                    "         \"name\" : \"Sydney Darling Harbour Hotel\",\n" +
                    "         \"place_id\" : \"ChIJbVjtNjeuEmsRq-6prwTK24Y\",\n" +
                    "         \"rating\" : 3.8,\n" +
                    "         \"reference\" : \"CnRwAAAAluhs8FHb8ZGMJdHXyHTbt7Ita4bqu0P241fd6pmQfZR7qfzx_ULBJEtvh3X4iqp_4OSOaYblaAz_2IpT2qdQG_3iUgBGqZQrYrDRFqtvm62xnZ4FrnUDUJruD3LKKOVbbgtrw_zR6sxtRJbluxOiFhIQIPm_a1N8XLTaWkwx9WvJxxoUlTRJr_QDQb8iKPaB0QUTZtvM7-0\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"lodging\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"11 Pyrmont Bridge Road, Sydney\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.866651,\n" +
                    "               \"lng\" : 151.195827\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86656485,\n" +
                    "                  \"lng\" : 151.196029\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86690944999999,\n" +
                    "                  \"lng\" : 151.195221\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"4f89212bf76dde31f092cfc14d7506555d85b5c7\",\n" +
                    "         \"name\" : \"Google\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1365,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/105932078588305868215/photos\\\"\\u003eMaksym Kozlenko\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAGtX76X3hb7lT6MuSjIqnAHe_3pJXRyQL8PodeDgx07UJNIWEGFllQKg1P9Yb0blcwQ_GyiRumYIQk4sxY9UtThq095kX85WcnPnrAOtxD7g6gWcVGUGUdljxLt-TnMaob6xZncP5sRto-T5luLDrztLBln8HgeUukPgyGIW4NJHEhCKiorfjbkayxoZtbFNgq-UGhTONmy8EwYc6sTF8jLEQtrKZJo5tA\",\n" +
                    "               \"width\" : 2048\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJN1t_tDeuEmsRUsoyG83frY4\",\n" +
                    "         \"rating\" : 4.6,\n" +
                    "         \"reference\" : \"CmRaAAAAlvYCP5vzEmwpwL3QMpIik3Y4S6Eiq9htTTRTNLcK1fPCvLFNE_WbimWuUqJ4vH9omnRWvLkYbOITuEpa4J5h3wcKjOaesHelk4pZq4q7-6NekYXIGZhGXGk-r7WsKl2KEhBqgsnlui4ZxEex6FaR12OsGhS0ff-Q4wRuYTxJrvU_FFsvDu1BqA\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"5, 48 Pirrama Rd, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.868016,\n" +
                    "               \"lng\" : 151.195141\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86580425,\n" +
                    "                  \"lng\" : 151.1973838\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.87093845,\n" +
                    "                  \"lng\" : 151.1929602\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/lodging-71.png\",\n" +
                    "         \"id\" : \"44fdc977984610cd873c790a7d850e8185ed0e5e\",\n" +
                    "         \"name\" : \"The Star\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 992,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/113847902696343041106/photos\\\"\\u003eThe Star\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAG6tcwAkYCUDic8ZK4w1aFJbE9pbpkNNiYHy2I2279OA6pH9f5FrhV3Ob4lWmZXaZL_6YgVhmq0sJlSOX8XkKFFN-wqrQWH36Vg6_blsiTJ8627clU7t8SFZRbzA85Mha3fjfC5yU35d3tAL4WT3UED4L5rXPA2z13TLW4PMAGcDEhAHX84M1rJpv-nkhciVTEPoGhQd3_sORFmWqi-ekxyPamAAqbYWPg\",\n" +
                    "               \"width\" : 829\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJq6qq6jauEmsRJAf7FjrKnXI\",\n" +
                    "         \"rating\" : 3.8,\n" +
                    "         \"reference\" : \"CmRbAAAAb5CET-cRHjDuFfYv4mffUyy0-8AI-mD_V-DSsRwdzoceCl8eQ7hdVbBvfbVP-g-lHmYCejCtysgkPK72aZSGokg_mxlIzMFE8Nn2PTBTioTCT2Y6Q8NcbARlxjEm8qkSEhCyBT7XCkxBqPcYnTYaNZaeGhRD-ZxtUAFvrGqkrdQRg1_dZw_cAA\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [\n" +
                    "            \"casino\",\n" +
                    "            \"spa\",\n" +
                    "            \"lodging\",\n" +
                    "            \"restaurant\",\n" +
                    "            \"food\",\n" +
                    "            \"point_of_interest\",\n" +
                    "            \"establishment\"\n" +
                    "         ],\n" +
                    "         \"vicinity\" : \"80 Pyrmont Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.86350300000001,\n" +
                    "               \"lng\" : 151.194921\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86319275000002,\n" +
                    "                  \"lng\" : 151.1957149\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86443374999999,\n" +
                    "                  \"lng\" : 151.1925393\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"64962a18758210c49bb59bdf6f1f76c333a70e1b\",\n" +
                    "         \"name\" : \"Jones Bay Wharf\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1536,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/116834444001829540421/photos\\\"\\u003eMaurice van Creij\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAPhmTEGrgg9hlyu_2C2S1kHw-eJ0PFVyQRnfB7kH7sXa6P2zU9B0fOn4e6pkrb6CIcFD0M59zfh6A2ZJqAQIZB77vBdsDL9reKX-5hufNnXw9371Y4PG8nKKjbjIC74pmdqDKcj28A8N14YMM-0VB7px9UQx_0YzfgdkEn7GgSctEhARlGGbpe4XTC4uL06_QjbiGhRp4N6t8i70fOHWb8iahjMIrJ6E0g\",\n" +
                    "               \"width\" : 2048\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJw1jiuEmuEmsRic0640-IS-k\",\n" +
                    "         \"rating\" : 4.7,\n" +
                    "         \"reference\" : \"CnRjAAAAGRIHAc0sixUC0tZ3b9Fp7pIMSD7smTPJuJhI2uaZ7FwUXIJAhTc_fabFV4i8LfYeI97M4QL0uZiL8GXmBeWTn640V93SxAfSCd_28ib2aaihi9GMoDdXxMczTspT_B8W3GI8DgGb2WhqswHSwCH9FBIQNZOXCsh8VcpN0WQQK5YKNBoUKEaWxSIFxJ8-6Dd4VQb0J1MTVJQ\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"real_estate_agency\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"26-32 Pirrama Road, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8693567,\n" +
                    "               \"lng\" : 151.1986328\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86928099999999,\n" +
                    "                  \"lng\" : 151.19883065\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.8695838,\n" +
                    "                  \"lng\" : 151.19803925\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/museum-71.png\",\n" +
                    "         \"id\" : \"45ffedee792e73666af7df087facf2ddb4509085\",\n" +
                    "         \"name\" : \"Австралийский национальный морской музей\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1520,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114788552898778216567/photos\\\"\\u003eraymond zhang\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBdwAAAKRVzGp_yFU21Gl_qBOVonCQminPF77Odq-KwBdIJ1A97aDgkmMdqMxnztBPX5tjf8Dd2bB_NM4JZ3i_W3eTvBBRw7HCwvOBGOAJQk_OHkQsqv2Ll24W6YD2aY8byqOH19BT60eheNTbOGfPq4QvrHAqNJvD0Y7YA82V95xlAzoTEhAKHINHoZoDiykeK8TKwbDwGhQGNbUunbPV8LExgE99s2r679IqGg\",\n" +
                    "               \"width\" : 2688\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJTze93zmuEmsRhvE6T4Y9DhU\",\n" +
                    "         \"rating\" : 4.2,\n" +
                    "         \"reference\" : \"CrQBoQAAAHl5J7QbX4rnVmsbFo9WDxd_JtuGg9899sx6w_NVRU5zMWUlsKSXt2k6QeHztHXy6TeztVvs-zaIURECYayD1YM4yOlfhjHfvgs_3cYs5iTrAwIUHdjI1-DpE4XVmiX9DemU3e-hiYpI4tqNtUu2aXqVZ6lutWpCJ2d1Up54-BJjKoVv27YTCVafjEsn4AKJsFAGIwdpXNtw9FACFAouIBN6fdDTkXfpKwXcof9JQ2mjEhD_4JMU-DF1puNGQ6Ry4kjxGhSV7g_cW6m8Gtm0psyNb0gWjsfOsg\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"museum\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"2 Murray Street, Sydney\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.86278079999999,\n" +
                    "               \"lng\" : 151.1952315\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86174989999998,\n" +
                    "                  \"lng\" : 151.19592275\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.8658735,\n" +
                    "                  \"lng\" : 151.19315775\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"05bf6e9aa18b35f174f5076c348ce8e91e328aba\",\n" +
                    "         \"name\" : \"Flying Fish Restaurant & Bar\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 2448,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/118410684014024830328/photos\\\"\\u003eMichael Mak\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAAHcdRiTByUl5XxK04fAQX2jpbEgskDB54NyJzJ-XGrNxnmI7FD6e1fUvi_yD1hIFn3j38PK1G-_2UP3K3p0_J0DaaZ1-dbK4tCF629jKiNVOZMqKGaJ-R8qgdPOU5o7UhEh0K0WBcbaCIYMOytmRzlztiuUnClsLOanlna1QJhzEhBxxeuU7l3Y-e27HJ3fNyuoGhRLDJcZmco8d-yIrc6UkUazV1RJTA\",\n" +
                    "               \"width\" : 3264\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJm7Ex8UmuEmsR37p4Hm0D0VI\",\n" +
                    "         \"price_level\" : 4,\n" +
                    "         \"rating\" : 4.4,\n" +
                    "         \"reference\" : \"CnRvAAAAnclvA1A6OTVnxLcPY16mkzL4JaDZQgZjR5vmlrhoIN8ltx7p_0uR3TfNF0kVoK6OoPhsph5ueSRpowObJerZPXJBtTiG2RRdDOzqlCyBz_-LPZXfL764J9vl2-gb51AQU3-46OfZUL8rRxqBxNuXoRIQMSdWbYQ-KeLLOV3GYwMCeRoUYeSxbKnJInRDDmLLtUMthfdKWA0\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"bar\", \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"Lower Deck, Jones Bay Wharf, 21 Pirrama Road, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.86350300000001,\n" +
                    "               \"lng\" : 151.194921\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86319275000002,\n" +
                    "                  \"lng\" : 151.1957149\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86443374999999,\n" +
                    "                  \"lng\" : 151.1925393\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"f01affffaff254a9b812eaffa905c3fb728c854b\",\n" +
                    "         \"name\" : \"Doltone House\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 2988,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110537423144561107284/photos\\\"\\u003eChris Lloyd\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBdwAAAPew-oiInYuLiGrHACTH0tnawp4xun__L8hwOXkMDnK11xCuf2JDcWX9So0G-nORI_aRTKcKRO9VY777qtUvoLD136IEkvD8VENY0Vual6ARV3uPFmENnxQ1zEO4RpjEr3aFbWXQOAdunOC5-wPjbGektHtyx3EUNYE8N_tXJbUUEhB9rE-qTCHqzMWL7mS86nzHGhRbqgbX-Gt9g04-2WEouIxaDxyFmQ\",\n" +
                    "               \"width\" : 5312\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJr9ZMJD6uEmsRT5yQWJvTmd0\",\n" +
                    "         \"price_level\" : 2,\n" +
                    "         \"rating\" : 4,\n" +
                    "         \"reference\" : \"CnRhAAAAdz-ohTaK0YvZ2-z70nKiJ_5l_sNKUFguPaTtOps5kmbW1aZ9fyd9hV8yFf9-9dSZjVXVt06_pSfgXPOesKnZEtZMA_SJjgwfgEuM6roYpn4bPLYWcidclVA_0y_O2SEMWE64LJ4nEdNIffXhn_zsVhIQObpZZZa9gsbxCTfeN1UnzBoUp_PsVNk13saD_SBsJHdjGa76hIs\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"26-32 Pirrama Road, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8685682,\n" +
                    "               \"lng\" : 151.1962433\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.8680639,\n" +
                    "                  \"lng\" : 151.1965076\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.8687363,\n" +
                    "                  \"lng\" : 151.1961552\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"172b11b0164ac9ae1f16c6802ed759445f6eacb7\",\n" +
                    "         \"name\" : \"Sydney Lyric Theatre\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 800,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/106181226772401075883/photos\\\"\\u003eSydney Lyric Theatre\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBdwAAAMe0eXH9Z5-nmi5OLRZyOUCCnE8VMN_7VN9x7gRHUqpYFv0TYqZsrz5m1YnFf7GKsTV6tWgVSgkK1zsTzRVjNkmslwG4qyrNIA6XNLmlir4PlBK4gYFpsyHiac9IjuGotYu_kNsH4OKOY9GtqGoQq9E4EqshcFma339Szi_3xmGoEhAJGYH0Vb2v1K4QA6O9mLxvGhTPMJxQsWPk51-Fv6QAaYhllKAxgQ\",\n" +
                    "               \"width\" : 801\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ1-v38TauEmsRxXV8BJ53Fq4\",\n" +
                    "         \"rating\" : 4.2,\n" +
                    "         \"reference\" : \"CnRoAAAAWeawwaGwp4VKD2xX1Z5bBG6hyYe4rNYgEvZ_f5kHXgx-QryrD_oq50hmMzNbXPbi_0ZddcZnwPLKxigYfChNHhQzb_ZKArGKYsFzVAnAzLuRgEs_vGXXsfNgk4tw_MJxDB9S8OQ5P65I4TPLjFIuNRIQs771umSuVExCxhwAsGKLqBoUUJXFgUAntdiqH-jRykLL5cCthqg\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"Pirrama Road, Pyrmont, Sydney\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8677499,\n" +
                    "               \"lng\" : 151.1956285\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86747284999999,\n" +
                    "                  \"lng\" : 151.19629035\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86858105,\n" +
                    "                  \"lng\" : 151.19364295\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"61f6d719f3d208ab2875bb2bfa164029c609c797\",\n" +
                    "         \"name\" : \"Avis\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : true,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 2448,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/110282762934738586599/photos\\\"\\u003eCorey Brand\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBdwAAABYBKLJgqYzWoX_2TZjcCS8sINxLzwNqbJMxktYnBvGTqapW2SzLhBiYpTY0kPzlg2Cs7HVeUSgtAExQNpcggzHAMGChspNOUSUmynuiLmmYI4edBOw3qdECqW2xoAUcx7rxqU1bCWM22rurHWuOSy61gLoBYYAmnmfmDsAvPsxyEhCYcpWgsKTyfQxOKnZ8s8kvGhRXdBmcPA8pjTA6C5rto0NybYjxZg\",\n" +
                    "               \"width\" : 3264\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJHTvh5jauEmsRRVTSKo0XD94\",\n" +
                    "         \"reference\" : \"CmRYAAAAiKKsrnIJ-c1DwrQPVA7tAjFFBqe4chdupwSlZaL9zRAlJElPYPtNTpItEbPQwCSjxegkkAWb0NLnkyxP0qaAcuPcIGg_Y8OshUWNrX3ZHupoaNA-UZvIIfP5sgvM6EpMEhDUJv4sZluEJPLgICZWzOaIGhRyK5MVC-JIFd-a2zYvNUzeUggq6Q\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"car_rental\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"80 Pyrmont Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8666485,\n" +
                    "               \"lng\" : 151.1957655\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86657534999999,\n" +
                    "                  \"lng\" : 151.1959457\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86686795000001,\n" +
                    "                  \"lng\" : 151.1952249\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"3ef986cd56bb3408bc1cf394f3dad9657c1d30f6\",\n" +
                    "         \"name\" : \"Doltone House - Darling Island Wharf\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 3096,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117659175401116409379/photos\\\"\\u003eReza Kahlaee\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBdwAAALb4FKpPM-LyQvqafQ21K46EIDXoNvmaJhR3UdgToZhCGbsRks6XNnFl3FY_5wmauHiKaKkvNIepyXDaG8AyE_-L4_1_ZRaVceZWDJL2E6ohAV5mL5uUEtuBNyU84Xcy1_ARyZ9GFxiiTc6DEf1tM4L69RSBaSoTsfSSZvgbx94vEhDRMAM9JrRBdeFHqNM3GijmGhQNo2gdQxyIHGN7xl0vP169rq_SbA\",\n" +
                    "               \"width\" : 4128\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ5xQ7szeuEmsRs6Kj7YFZE9k\",\n" +
                    "         \"rating\" : 4.2,\n" +
                    "         \"reference\" : \"CoQBeAAAAEAZJGs-gpKk43_3PRtGyMTfWr5J48JAKSt_1ZJC_Ggd73r_ht-JvABW28U6UeRJwqf_c7MozQMl4Qmm1DE8UlgWjN0ZL_-d70NyMVj0HtckEDY9T2NEqIG001_xickyK4EBPbh5xyINTemBuXR5jJF1UMkFq67_AtmjvpOMd25LEhDMcxbE8dsjPCcAyUF1XjBUGhR_5W4mcOFEox8bRYDcGWFreUkkFQ\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"Ground, 48 Pirrama Road, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8657776,\n" +
                    "               \"lng\" : 151.1949072\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.8656057,\n" +
                    "                  \"lng\" : 151.19510545\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.8662933,\n" +
                    "                  \"lng\" : 151.19431245\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_business-71.png\",\n" +
                    "         \"id\" : \"19cdf89322aa1fc3523c583a74a27645b11430e8\",\n" +
                    "         \"name\" : \"Seven Group Holdings Limited\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 4032,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/106141648197938449131/photos\\\"\\u003eBrendan Hong\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAILrJJFEyTTvl8ZgYX9v9ixYLBMg0xvnfIugQLAQBVRo9n0c-vxTP3B9WyoxP-UfBKut6sxsqyG0xD0xwfExFusJbE6ATqeK-eXUu5Ja_nwRyp1AIbRadJ15RLFeQ1FsgABX7P8f1uF7iHwR2Bm-3IAdz1JfAeebC4D5GIPjZ51EEhC_xQ2FOYtvzjTheEpcByFMGhQ5rpAghcc76cIDnHojzArHunbPJQ\",\n" +
                    "               \"width\" : 3024\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJz2EHuEmuEmsR9FZySlT-kNI\",\n" +
                    "         \"reference\" : \"CnRwAAAA6TEdyiqc1AqmRnO1kAGqeA8qs35nu_oMc0ngXzNbFS9FsoipfGR3Qq3HFSXgaUboNcyMj7RUEpNKLMiFayrR_zs5BK8iokqY6axq3GxwZoanj2WXDBYJeZ4fdAuThIWkCghqD3kJHLlIBSNfYoxV7hIQbK49KuqLdvkGPzdroFA02xoUwH8n0hqpIu_IustbS09EAArQzOY\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"38-42 Pirrama Road, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8679791,\n" +
                    "               \"lng\" : 151.1945504\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.86782085000001,\n" +
                    "                  \"lng\" : 151.19467565\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.86845385,\n" +
                    "                  \"lng\" : 151.19417465\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png\",\n" +
                    "         \"id\" : \"a946d56ed2464a60ce54305c2f011e71617a7214\",\n" +
                    "         \"name\" : \"Flying Fish & Chips\",\n" +
                    "         \"opening_hours\" : {\n" +
                    "            \"open_now\" : false,\n" +
                    "            \"weekday_text\" : []\n" +
                    "         },\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 992,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109058915883256959588/photos\\\"\\u003eFlying Fish &amp; Chips\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAAIIuIaTwyeg-psJZtOVUJBPh5hNozgr91PaNvrz1v_8RpWcOkB7ozhX4hGPjUwXccKZdOKfoYsQTJdJpG7JC6NxxYA_ogZzA10NM5lAWFCEGoOz0QkFivBCHZl4XcdHIXWJKfQ97u_c1uIswbFonIdskfIVYytCXW7mvaK6Gs0SgEhAWSYxUq3n9WqDBbHNisBzpGhQRlcbO-uYR9tndm57cZ_kz1vWZ5A\",\n" +
                    "               \"width\" : 1494\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJ1-v38TauEmsRM5ybcRx1Zc0\",\n" +
                    "         \"rating\" : 3.2,\n" +
                    "         \"reference\" : \"CnRnAAAAJWTpIALqY9z6ADQUcpysAlQsXSMvCEmMA09DEW6nQjhh-iIabuejSDM2HOabFrjf6zUmE6fK2pzcqyE9AW9mH_OcpvAjk-GSIiv-vVqQ5fW5N2cMbWOO_JV2ZlVEHs1prB9Euzhgb1eokaj_J7L09BIQlUHg9nHP6A0qDFPsV0S3_hoUThLrGymj2ZP6VxxaWk9ZpwU9-iU\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"restaurant\", \"food\", \"point_of_interest\", \"establishment\" ],\n" +
                    "         \"vicinity\" : \"80 Pyrmont Street, Pyrmont\"\n" +
                    "      },\n" +
                    "      {\n" +
                    "         \"geometry\" : {\n" +
                    "            \"location\" : {\n" +
                    "               \"lat\" : -33.8688197,\n" +
                    "               \"lng\" : 151.2092955\n" +
                    "            },\n" +
                    "            \"viewport\" : {\n" +
                    "               \"northeast\" : {\n" +
                    "                  \"lat\" : -33.8561088,\n" +
                    "                  \"lng\" : 151.222951\n" +
                    "               },\n" +
                    "               \"southwest\" : {\n" +
                    "                  \"lat\" : -33.8797034,\n" +
                    "                  \"lng\" : 151.1970328\n" +
                    "               }\n" +
                    "            }\n" +
                    "         },\n" +
                    "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png\",\n" +
                    "         \"id\" : \"33e35d1925474acf2e3a2a509144aff306ef7962\",\n" +
                    "         \"name\" : \"Сидней\",\n" +
                    "         \"photos\" : [\n" +
                    "            {\n" +
                    "               \"height\" : 1371,\n" +
                    "               \"html_attributions\" : [\n" +
                    "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/105979738839574511049/photos\\\"\\u003eJan Hoppe\\u003c/a\\u003e\"\n" +
                    "               ],\n" +
                    "               \"photo_reference\" : \"CoQBcwAAADp7-hYgxR_9GRJuLg9-6jm22ZuuOjoJO_5kUsaDHOxBR7j-o6VI_XneNq0IyliYnvj9jtbwUN88J3aeoPJULNySTTiYxVCpLQBFtKyOS90PXtXIPbGLoVQQZ3UAyafsM0eZA7aRwPcKYCq9hkS9XgT3SE_evbheHuU8oTd858BXEhAjsfxzuf_GJJt42G6TBOQIGhTdFcDvEqMEGKo7lEiML9UI8gcD-w\",\n" +
                    "               \"width\" : 2048\n" +
                    "            }\n" +
                    "         ],\n" +
                    "         \"place_id\" : \"ChIJP5iLHkCuEmsRwMwyFmh9AQU\",\n" +
                    "         \"reference\" : \"CqQBnAAAAL3SoxTU5nQXCUApKYMrUXcIzxEnn_IwRn2geQLD-SUxQ-QDTz-bx8WKKEt78Ob1l4N17t3nc_Ocvsj1yddG7b2dUisKvOixR7_XbC9cN1Hy3DtXY1SZBqUm1wF6BCC8AA1fDxUKMdpv13JSm-EFj5R1c-p2cdKpY-liTr26gJhmAuZcJN84uEi1BXPBLYajXtq1xfi7BHCX98bj2sMBJ9QSEG9brxJ-tsnOnDbhSeKubXMaFACVG6A5PDLBFy1Xi0saG9L8pHGY\",\n" +
                    "         \"scope\" : \"GOOGLE\",\n" +
                    "         \"types\" : [ \"locality\", \"political\" ],\n" +
                    "         \"vicinity\" : \"Сидней\"\n" +
                    "      }\n" +
                    "   ],\n" +
                    "   \"status\" : \"OK\"\n" +
                    "}\n";
            super.onPostExecute(strJson);
            try {
                JSONObject dataJsonObj = new JSONObject(strJson);
                JSONArray results = dataJsonObj.getJSONArray("results");

                Place[] places = new Place[results.length()];
                for (int i = 0; i < results.length(); i++) {
                    JSONObject place = results.getJSONObject(i);
                    JSONObject location = place.getJSONObject("geometry").getJSONObject("location");
                    places[i] = new Place(place.getString("name"), (float)location.getDouble("lat"), (float)location.getDouble("lng"));
                }
                mListener.onPlacesGet(places);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
