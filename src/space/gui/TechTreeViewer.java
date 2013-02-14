/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package space.gui;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import space.engine.TechTree;
import space.engine.Technology;
import vytah.algorithms.Predicate;
import vytah.functional.Function;
import vytah.functional.Lists;

/**
 *
 * @author karol
 */
public class TechTreeViewer extends JFrame implements ActionListener{
	public JList list;
	public int currTech=0;
	public JLabel techName=new JLabel(" ");
	public JLabel techDesc=new JLabel(" ");
	public JButton[] prereq=new JButton[10];
	public JButton[] postreq=new JButton[10];
	public int[] calculatedPrereqs = new int[10];
	public int[] calculatedPostreqs = new int[10];

	public TechTreeViewer(){
		String[] technames=Lists.mapToArray(new Function<Technology,String>(){
			@Override public String apply(Technology x) {return x.name;}
		}, TechTree.TREE, String.class);
		Arrays.sort(technames);
		setLayout(new FormLayout("5dlu,100dlu,5dlu,100dlu,pref:grow,100dlu,5dlu", 
				"5dlu,pref,5dlu,pref,5dlu,pref,"//heADERS
				+ "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,"//buttons
				+ "5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,5dlu,pref,"//buttons
				+ "5dlu,pref:grow,5dlu,pref,5dlu"));
		CellConstraints cc = new CellConstraints();

		list=new JList(technames);
		list.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent me) {
				TechTreeViewer.this.readTechnology(list.getSelectedValue().toString());
			}
			@Override public void mousePressed(MouseEvent me) {}
			@Override public void mouseReleased(MouseEvent me) {}
			@Override public void mouseEntered(MouseEvent me) {}
			@Override public void mouseExited(MouseEvent me) {}
		});
		JScrollPane pane=new JScrollPane(list);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(pane,cc.xywh(2, 2, 1, 28));
		for(int i=0; i<prereq.length;i++){
			prereq[i]=new JButton();
			prereq[i].addActionListener(this);
			
			add(prereq[i],cc.xy(4, 8+2*i));
		}
		for(int i=0; i<postreq.length;i++){
			postreq[i]=new JButton();
			postreq[i].addActionListener(this);
			add(postreq[i],cc.xy(6, 8+2*i));
		}
		add(techName,cc.xyw(4,2,3));
		add(techDesc,cc.xyw(4,4,3));
		add(new JLabel("Prerequisites:"),cc.xy(4,6));
		add(new JLabel("Allows:"),cc.xy(6,6));
		list.setSelectedIndex(0);
		readTechnology(list.getSelectedValue().toString());
		setSize(700,550);
	}
	final void readTechnology(String name){
		Technology tech=null;
		int techI=0;
		for(int i=0; i<TechTree.TREE.length; i++){
			if(TechTree.TREE[i].name.equals(name)){
				tech=TechTree.TREE[i];
				techI=i;
				break;
			}
		}
		final int finalTechI=techI;
		if(tech==null) return;

		list.setSelectedValue(tech,true);
		techName.setText(tech.name);
		techDesc.setText(tech.name+" HERP DERP");

		for(JButton b: postreq) b.setVisible(false);
		for(JButton b: prereq) b.setVisible(false);
		int pqC=0;
		for(int pq : tech.prerequisites){
			prereq[pqC].setVisible(true);
			prereq[pqC].setActionCommand(TechTree.TREE[pq].name);
			prereq[pqC].setText(TechTree.TREE[pq].name);
			pqC++;
			if(pqC==prereq.length) break;
		}
		List<Technology> postr=new Predicate<Technology>(){
			@Override
			public boolean hasProperty(Technology object) {
				return object.prerequisites.contains(finalTechI);
			}
		}.selectList(TechTree.TREE);
		pqC=0;
		for(Technology t:postr){
			postreq[pqC].setVisible(true);
			postreq[pqC].setActionCommand(t.name);
			postreq[pqC].setText(t.name);
			pqC++;
			if(pqC==postreq.length) break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String command=ae.getActionCommand();
		if(command==null|| command.isEmpty()) return;
		readTechnology(command);
	}
}
