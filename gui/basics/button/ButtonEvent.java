package gui.basics.button;

public enum ButtonEvent {

	press(1), alternatepress(2), release(3), alternateRelease(4);

	private ButtonEvent(int ID) {
		this.ID = ID;
	}

	int ID;

}
