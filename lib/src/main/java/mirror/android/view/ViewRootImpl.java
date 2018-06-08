package mirror.android.view;

import mirror.MethodReflectParams;
import mirror.RefClass;
import mirror.RefMethod;
import mirror.RefObject;

public class ViewRootImpl
{
  public static Class<?> TYPE = RefClass.load(ViewRootImpl.class, "android.view.ViewRootImpl");

  @MethodReflectParams({"android.view.InputEvent", "android.view.InputEventReceiver", "int", "boolean"})
  public static RefMethod<Void> enqueueInputEvent;
  public static RefObject<Object> mInputChannel;
  public static RefObject<Object> mInputEventReceiver;

  public static class WindowInputEventReceiver
  {
    public static Class<?> TYPE = RefClass.load(WindowInputEventReceiver.class, "android.view.ViewRootImpl$WindowInputEventReceiver");
    public static RefMethod<Void> dispose;
  }
}
