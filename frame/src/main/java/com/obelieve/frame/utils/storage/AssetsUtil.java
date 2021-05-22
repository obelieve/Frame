package com.obelieve.frame.utils.storage;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zxy on 2018/9/19 15:15.
 */

public class AssetsUtil
{

    public static String getAssetsContent(Context context, String fileName)
    {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try
        {
            InputStream in = context.getAssets().open(fileName);
            InputStreamReader streamReader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(streamReader);
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(temp + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }finally
        {
            if(bufferedReader!=null){
                try
                {
                    bufferedReader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}
