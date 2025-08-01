// Generated by view binder compiler. Do not edit!
package com.example.booksalesmanagement.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.booksalesmanagement.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NavHeaderBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final CircleImageView iconImage;

  @NonNull
  public final TextView tvNavEmail;

  @NonNull
  public final TextView tvNavUsername;

  private NavHeaderBinding(@NonNull RelativeLayout rootView, @NonNull CircleImageView iconImage,
      @NonNull TextView tvNavEmail, @NonNull TextView tvNavUsername) {
    this.rootView = rootView;
    this.iconImage = iconImage;
    this.tvNavEmail = tvNavEmail;
    this.tvNavUsername = tvNavUsername;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NavHeaderBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NavHeaderBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.nav_header, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NavHeaderBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iconImage;
      CircleImageView iconImage = ViewBindings.findChildViewById(rootView, id);
      if (iconImage == null) {
        break missingId;
      }

      id = R.id.tv_navEmail;
      TextView tvNavEmail = ViewBindings.findChildViewById(rootView, id);
      if (tvNavEmail == null) {
        break missingId;
      }

      id = R.id.tv_navUsername;
      TextView tvNavUsername = ViewBindings.findChildViewById(rootView, id);
      if (tvNavUsername == null) {
        break missingId;
      }

      return new NavHeaderBinding((RelativeLayout) rootView, iconImage, tvNavEmail, tvNavUsername);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
