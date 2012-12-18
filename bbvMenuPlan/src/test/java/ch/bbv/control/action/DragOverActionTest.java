package ch.bbv.control.action;
import static org.mockito.Mockito.*;

import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import org.junit.Ignore;
import org.junit.Test;


public class DragOverActionTest {
	@Test
	@Ignore("Dragboard ist final...")
	public void handle_notSameAndHasString_moveAccepted() {
		DragOverAction testee = new DragOverAction();
		DragEvent eventMock = mock(DragEvent.class);
		when(eventMock.getTarget()).thenReturn(mock(ListView.class));
		Dragboard dbMock = mock(Dragboard.class);
		when(dbMock.hasString()).thenReturn(Boolean.TRUE);
		when(eventMock.getDragboard()).thenReturn(dbMock);
		testee.handle(eventMock);
		verify(eventMock).acceptTransferModes(eq(TransferMode.MOVE));
	}

}
