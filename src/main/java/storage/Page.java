package storage;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import constants.Constants;
import exceptions.DBAppException;
import filecontroller.FileCreator;
import filecontroller.FileDeleter;
import filecontroller.FileType;
import filecontroller.Serializer;
import search.PageSearch;

public class Page implements Serializable {
	private String name;
	private int maxRows;
	private Vector<Tuple> tuples;
	private Object minPK, maxPK;
	private int size;
	private String tableName;

	public Page(String tableName) {
		this.tuples = new Vector<>();
		this.tableName = tableName;
	}

	public Object getMinPK() {
		return minPK;
	}

	public Vector<Tuple> getTuples() {
		return tuples;
	}

	public void setMinPK(Object minPK) {
		this.minPK = minPK;
	}

	public Object getMaxPK() {
		return maxPK;
	}

	public void setMaxPK(Object maxPK) {
		this.maxPK = maxPK;
	}

	public int getSize() {
		return size;
	}

	public String getTableName() {
		return tableName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public boolean isFull() {
		return size == maxRows;
	}

	protected Tuple removeLastTuple() {
		return tuples.remove(tuples.size() - 1);
	}

	protected void insertIntoPage(Tuple tuple) throws IOException, DBAppException, ParseException {
		int position = pageBinarySearch(tuple.getPrimaryKey());
		tuples.add(position, tuple);
		size++;
		Serializer.SerializePage(name, this);
		newMinMax();
	}

	private void newMinMax() {
		minPK = tuples.get(0).getPrimaryKey();
		maxPK = tuples.get(tuples.size() - 1).getPrimaryKey();
	}

	private int pageBinarySearch(Object primaryKey) throws DBAppException, ParseException {
		return PageSearch.binarySearch(this, primaryKey);
	}

	protected Vector<Tuple> linearSearch(String colName, Object value) throws DBAppException, ParseException {
		return PageSearch.linearSearch(this, colName, value);
	}

	protected void DeleteFromPage(Tuple tuple) throws IOException, DBAppException, ParseException {
		int position = pageBinarySearch(tuple.getPrimaryKey());
		if (position != -1) {
			tuples.remove(position);
			size--;
			newMinMax();
			Serializer.SerializePage(name, this);
			handleEmptyPage();
		} else {
			throw new DBAppException(Constants.ERROR_MESSAGE_SEARCH_NOT_FOUND);
		}
	}

	private void handleEmptyPage() throws IOException {
		if (tuples.isEmpty())
			deletePageFile();
	}

	private void deletePageFile() {
		FileDeleter.deleteFile(this,FileType.PAGE);
	}

	protected void createPageFile() throws IOException {

		FileCreator.createFile(this, FileType.PAGE);
	}

	protected void updateTuple(Object clusteringKeyValue, Hashtable<String, Object> htblColNameValue)
			throws DBAppException, ParseException {
		int pkVectorPoition = pageBinarySearch(clusteringKeyValue);
		Tuple tuple = tuples.get(pkVectorPoition);

		for (Cell c : tuple.getCells()) {
			c.setValue(htblColNameValue.get(c.getKey()));
		}

	}
}
