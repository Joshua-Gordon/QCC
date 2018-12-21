package utils.customCollections.eventTracableCollections;

import java.io.Serializable;

public class Notifier implements Serializable {
	private static final long serialVersionUID = -4355135792535307514L;
	
	private static final Notifier NO_NOTIFIER;
	
	static {
		
		NO_NOTIFIER = new Notifier() {
			private static final long serialVersionUID = -7938646971850743846L;
			@Override
			public void sendChange(Object source, String methodName, Object... args) {}
			@Override
			public void setReceiver(Notifier receiver) {}
		};
		
		NO_NOTIFIER.receiver = null;
	}
	
	private Notifier receiver;
	private transient ReceivedEvent receivedEvent;
	
	public Notifier() {
		this(NO_NOTIFIER);
	}
	
	public Notifier(Notifier receiver) {
		this(receiver, null);
	}
	
	public Notifier(Notifier receiver, ReceivedEvent receivedEvent) {
		setReceiver(receiver);
		setReceivedEvent(null);
	}
	
	public void setReceiver(Notifier receiver) {
		this.receiver = receiver == null ? NO_NOTIFIER : receiver;
	}
	
	public void setReceivedEvent(ReceivedEvent receivedEvent) {
		this.receivedEvent = receivedEvent;
	}
	
	public void sendChange(Object source, String methodName, Object... args) {
		receiver.retrieve(source, methodName, args);
	}
	
	private void retrieve(Object source, String methodName, Object... args) {
		if(receivedEvent != null)
			receivedEvent.receive(source, methodName, args);
		sendChange(source, methodName, args);
	}
	
	public static interface ReceivedEvent {
		public boolean receive(Object source, String methodName, Object ... args);
	}
	
}
