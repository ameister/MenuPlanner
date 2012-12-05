package ch.bbv.control;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import ch.bbv.bo.Menu;


public class MenuControllerTest {
	
	@Mock
	private List<Menu> menuListMock;
	
	private MenuController testee;
	
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		testee = new MenuController(menuListMock);
	}
	
	@Test
	public void commit_currentMenu_menuAdded(){
		testee.createMenu();
		testee.commit();
		Mockito.verify(menuListMock).add(Mockito.any(Menu.class));
	}

}
