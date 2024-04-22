package example.txb.crypto_android.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class Ban_ViewCommand extends FragmentStateAdapter {
    Ban_OpenFragment ban_openFragment;
    Ban_CloseFragment ban_closeFragment;
    public Ban_ViewCommand(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        ban_openFragment = new Ban_OpenFragment();
        ban_closeFragment = new Ban_CloseFragment();
    }

    public void reloadData(){
        ban_openFragment.reloadData();
        ban_closeFragment.reloadData();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return ban_closeFragment;
            default:
                return ban_openFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
