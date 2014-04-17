package org.kamol.nefete.data.chat;

/**
 * Message is a Custom Object to encapsulate message information/fields
 * Credit to Adil Soomro
 */
public class Message {
  /**
   * The content of the message
   */
  String message;
  /**
   * boolean to determine, who is sender of this message
   */
  boolean isOwner;
  /**
   * boolean to determine, whether the message is a status message or not.
   * it reflects the changes/updates about the sender is writing, have entered text etc
   */
  boolean isStatusMessage;

  /**
   * Constructor to make a Message object
   */
  public Message(String message, boolean isOwner) {
    super();
    this.message = message;
    this.isOwner = isOwner;
    this.isStatusMessage = false;
  }

  /**
   * Constructor to make a status Message object
   * consider the parameters are swaped from default Message constructor,
   * not a good approach but have to go with it.
   */
  public Message(boolean status, String message) {
    super();
    this.message = message;
    this.isOwner = false;
    this.isStatusMessage = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public boolean isOwner() {
    return isOwner;
  }

  public void setMine(boolean isMine) {
    this.isOwner = isOwner;
  }

  public boolean isStatusMessage() {
    return isStatusMessage;
  }

  public void setStatusMessage(boolean isStatusMessage) {
    this.isStatusMessage = isStatusMessage;
  }
}