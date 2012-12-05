package ch.bbv.control;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.bbv.bo.Menu;
import ch.bbv.bo.Weekday;


public class MenuControllerTest {
	
	@Mock
	private List<Menu> menuListMock;
	@Mock
	private EntityManager entityManagerMock;
	@Mock
	private Weekday weekday;
	private MenuController testee;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		testee = new MenuController(menuListMock, weekday, entityManagerMock);
	}
	
	@Test
	public void commit_currentMenu_menuAdded() throws Exception{
		EntityTransaction transactionMock = mock(EntityTransaction.class);
		when(entityManagerMock.getTransaction()).thenReturn(transactionMock);
		testee.createMenu();
		testee.commit();
		verify(menuListMock).add(any(Menu.class));
		verify(transactionMock).commit();
		verify(entityManagerMock).persist(any(Menu.class));
	}

	@Test
	public void rollback_currentMenu_rollbackAndClear() throws Exception{
		EntityTransaction transactionMock = mock(EntityTransaction.class);
		when(entityManagerMock.getTransaction()).thenReturn(transactionMock);
		testee.createMenu();
		testee.rollback();
		verify(transactionMock).rollback();
	}
	@Test
	public void getCurrentMenu_currentMenuNull_menuCreated() throws Exception{
		Menu result = testee.getCurrentMenu();
		assertNotNull("Current Menu must be created", result);
	}

}
