package ch.bbv.ui;

import static org.junit.Assert.assertEquals;

import java.awt.Button;

import org.jemmy.fx.AppExecutor;
import org.jemmy.fx.SceneDock;
import org.jemmy.fx.control.LabeledDock;
import org.jemmy.resources.StringComparePolicy;
import org.junit.BeforeClass;
import org.junit.Test;

public class MenuPlannerTest {

	@BeforeClass
	public static void setUpClass() {
		AppExecutor.executeNoBlock(MenuPlanner.class);
	}

	@Test
	public void isAddMenuButtonThere() {
		@SuppressWarnings("unchecked")
		SceneDock scene = new SceneDock();
		// a button is a labeled, every labeled could be found by text
		assertEquals("Button must be there", Button.class, new LabeledDock(scene.asParent(), "Add Menu", StringComparePolicy.EXACT).wrap().getControl().getClass());

	}

}
