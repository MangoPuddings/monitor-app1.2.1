package com.onlydoone.busposition.bean;

import com.onlydoone.busposition.mInterface.TreeNodeAlias;
import com.onlydoone.busposition.mInterface.TreeNodeId;
import com.onlydoone.busposition.mInterface.TreeNodeLabel;
import com.onlydoone.busposition.mInterface.TreeNodeLevel;
import com.onlydoone.busposition.mInterface.TreeNodeOnline;
import com.onlydoone.busposition.mInterface.TreeNodePid;

public class Bean
{
	/**
	 * id
	 */
	@TreeNodeId
	private int id;
	/**
	 * 父id
	 */
	@TreeNodePid
	private int pId;
	/**
	 * 内容
	 */
	@TreeNodeLabel
	private String label;
	/**
	 * 节点级别
	 */
	@TreeNodeLevel
	private int level;
	/**
	 * 车辆是否在线
	 */
	@TreeNodeOnline
	private String online;
	@TreeNodeAlias
	/**
	 * 别名
	 */
	private String alias;

	public Bean()
	{
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public Bean(int id, int pId, String label)
	{
		this.id = id;
		this.pId = pId;
		this.label = label;
	}
	public Bean(int id, int pId, String label, int level,String alias) {
		this.id = id;
		this.pId = pId;
		this.label = label;
		this.level = level;
		this.alias = alias;
	}

	public Bean(int id, int pId, String label, int level, String online,String alias) {
		this.id = id;
		this.pId = pId;
		this.label = label;
		this.level = level;
		this.online = online;
		this.alias = alias;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getpId()
	{
		return pId;
	}

	public void setpId(int pId)
	{
		this.pId = pId;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

}
