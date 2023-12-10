package org.eclipse.petrinets.launch;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ToolTipOnRelease extends JPanel
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ToolTipOnRelease()
    {
        JLabel label = new JLabel( "First Name:" );
        add( label );

        JTextField textField = new JTextField(15);
        add( textField );

        MouseListener ml = new MouseAdapter()
        {
            public void mouseReleased(MouseEvent e)
            {
                JComponent component = (JComponent)e.getSource();
                component.setToolTipText("Mouse released on: " + component.getClass().toString());

                MouseEvent phantom = new MouseEvent(
                    component,
                    MouseEvent.MOUSE_MOVED,
                    System.currentTimeMillis(),
                    0,
                    0,
                    0,
                    0,
                    false);

                ToolTipManager.sharedInstance().mouseMoved(phantom);
            }
        };

        label.addMouseListener( ml );
        textField.addMouseListener( ml );
    }

    private static void createAndShowUI()
    {
        JFrame frame = new JFrame("ToolTipOnRelease");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add( new ToolTipOnRelease() );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible( true );
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowUI();
            }
        });
    }
}