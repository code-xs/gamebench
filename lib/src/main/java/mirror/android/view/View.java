package mirror.android.view;

import mirror.RefClass;
import mirror.RefMethod;

public class View
{
  public static Class<?> TYPE = RefClass.load(View.class, android.view.View.class);
  public static RefMethod<Object> getViewRootImpl;
}
