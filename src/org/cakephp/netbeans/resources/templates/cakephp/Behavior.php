<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

/**
 * CakePHP Behavior
 * @author ${user}
 */
class ${name}Behavior extends ModelBehavior {

	public $settings = array();

	function setup(&$model, $config = array()){
		$this->settings[$model->alias] = $config;
	}

	function cleanup(&$model) {
		parent::cleanup($model);
	}

	function beforeFind(&$model, $query){

	}

	function afterFind(&$model, $results, $primary){

	}

	function beforeValidate(&$model){

	}

	function beforeSave(&$model){

	}

	function afterSave(&$model, $created){

	}

	function beforeDelete(&$model, $cascade = true){

	}

	function afterDelete(&$model){

	}

	function onError(&$model, $error){

	}
}
