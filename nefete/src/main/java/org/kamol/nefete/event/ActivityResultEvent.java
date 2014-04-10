package org.kamol.nefete.event;

import android.content.Intent;

public class ActivityResultEvent {
    public final int requestCode;
    public final int resultCode;
    public final Intent data;

    public ActivityResultEvent(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }
}
