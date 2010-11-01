/*******************************************************************************
 * Copyright (c) 2010 Per Kr. Soreide.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Per Kr. Soreide - initial API and implementation
 *******************************************************************************/
package bndtools.release;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import bndtools.release.api.ReleaseContext.Error;

public class ErrorList {

	private List<Error> errors;
	private Composite container;
	
	public ErrorList(List<Error> errors) {
		this.errors = errors;
	}

	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, true));
		addErrorItems(container);
	}

	private void addErrorItems(Composite parent) {
		
		Composite comp = new Composite(container, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		comp.setLayout(gridLayout);
		comp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true,
				true));

		for (Error error : errors) {
			ErrorItem errorItem = new ErrorItem(error);
			errorItem.createControl(comp);
		}
	}

	public Control getControl() {
		return container;
	}
	
	public void dispose() {
		container.dispose();
		container = null;
	}

	public static class ErrorItem {

		private Error error;
		public ErrorItem(Error error) {
			this.error = error;
		}
		
		public void createControl(Composite parent) {
//			Composite c1 = new Composite(parent, SWT.NONE);
//			GridLayout gridLayout = new GridLayout();
//			gridLayout.numColumns = 1;
//			gridLayout.horizontalSpacing = 0;
//			gridLayout.verticalSpacing = 5;
//			gridLayout.marginWidth = 0;
//			gridLayout.marginHeight = 10;
//
//			c1.setLayout(gridLayout);
//			c1.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.VERTICAL, true, true));

			Group g = new Group(parent, SWT.SHADOW_ETCHED_IN);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			gridLayout.horizontalSpacing = 0;
			gridLayout.verticalSpacing = 5;
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 10;
   	        GridData gridData = new GridData();
		    gridData.horizontalAlignment = GridData.FILL;
		    gridData.grabExcessHorizontalSpace = true;
		    gridData.widthHint = 500;
			g.setLayout(gridLayout);
		    g.setLayoutData(gridData);
			
		    g.setText(error.getScope() + (error.getSymbName() == null ? "" : " :  " + error.getSymbName() + "-" + error.getVersion()));
		    
		    Composite c2 = new Composite(g, SWT.NONE);
			gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			gridLayout.horizontalSpacing = 0;
			gridLayout.verticalSpacing = 5;
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 10;

			c2.setLayout(gridLayout);
			gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
//		    gridData.horizontalAlignment = GridData.FILL;
//		    gridData.grabExcessHorizontalSpace = true;
			c2.setLayoutData(gridData);

			
			Label label = new Label(c2, SWT.NONE);
			label.setText("Message");

			Text text = new Text(c2, SWT.BORDER | SWT.MULTI);
			text.setEditable(false);
			text.setText(error.getMessage());

			if (error.getSymbName() != null) {
				label = new Label(c2, SWT.NONE);
				label.setText("Symbolic Name");
	
				text = new Text(c2, SWT.BORDER);
				text.setEditable(false);
				text.setText(error.getSymbName());

				label = new Label(c2, SWT.NONE);
				label.setText("Version");
	
				text = new Text(c2, SWT.BORDER);
				text.setEditable(false);
				text.setText(error.getVersion());
			}
			
			createTableViewer(g);
		}
		
		private void createTableViewer(Composite parent) {
			if (error.getList() == null) {
				return;
			}

			Composite c2 = new Composite(parent, SWT.NONE);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 1;
			gridLayout.horizontalSpacing = 0;
			gridLayout.verticalSpacing = 5;
			gridLayout.marginWidth = 0;
			gridLayout.marginHeight = 10;

			c2.setLayout(gridLayout);
			c2.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

			TableViewer viewer = new TableViewer(c2, SWT.MULTI | SWT.H_SCROLL
					| SWT.V_SCROLL | SWT.FULL_SELECTION);

			String[] headers = error.getHeaders();
			if (headers.length == 0) {
				headers = new String[error.getList().length];
				for (int i = 0; i < headers.length; i++) {
					headers[i] = "";
				}
			}
			
			for (int i = 0; i < headers.length; i++) {
				TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
				column.getColumn().setText(headers[i]);
				column.getColumn().setWidth(getWidth(c2, headers[i], i));
				column.getColumn().setResizable(true);
				column.getColumn().setMoveable(true);
			}
			Table table = viewer.getTable();
			table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

			if (error.getHeaders().length > 0) {
				table.setHeaderVisible(true);
			}
			table.setLinesVisible(true);
			
			viewer.setContentProvider(new ArrayContentProvider());
			viewer.setLabelProvider(new ArrayLabelProvider());

			viewer.setInput(error.getList());
		}
	    private int getWidth(Drawable cmp, String title, int colNo) {
	        String[][] table = error.getList();
	    	int maxLength = title.length();
	        for (int i = 0; i < table.length; i++) {
	    		int len = table[i][colNo].length();
	    		if (len > maxLength) {
	    			maxLength = len;
	    		}
	    	}
	        maxLength += 2;
	    	GC gc = new GC(cmp);
	        int charWidth = gc.getFontMetrics().getAverageCharWidth();
	        gc.dispose();
	        return charWidth * maxLength;
	    }

	}
	
	private static class ArrayLabelProvider extends LabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String[] cols = (String[]) element;
			return cols[columnIndex];
		}
	}

	private static class ArrayContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			String[][] table = (String[][]) inputElement;
			return table;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}

}
