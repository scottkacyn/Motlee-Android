package com.motlee.android.object;

import com.motlee.android.R;
import com.motlee.android.fragment.MainMenuFragment;
import com.motlee.android.view.HorizontalAspectImageButton;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

public class MenuFunctions {

	public static void goBack(Activity activity)
	{
		activity.finish();
	}
	
	public static void onClickOpenMainMenu(View view, FragmentActivity activity)
	{
    	FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        
        ft.add(R.id.main_menu, mainMenuFragment);
        
        HorizontalAspectImageButton menuButton = (HorizontalAspectImageButton) activity.findViewById(R.id.menu_button);
		
		menuButton.setEnabled(false);
        
        ft.commit();
        
        
	}
}
