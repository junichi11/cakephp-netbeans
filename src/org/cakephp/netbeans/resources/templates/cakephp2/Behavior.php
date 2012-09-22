<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends ModelBehavior {

	public function setup($model, $settings = array()){
		$this->settings[$model->alias] = $settings;
	}

	public function cleanup($model) {
		parent::cleanup($model);
	}

//	public function beforeFind($model, $query){
//
//	}

	public function afterFind($model, $results, $primary){

	}

//	public function beforeValidate($model){
//
//	}

//	public function beforeSave($model){
//
//	}

//	public function afterSave($model, $created){
//
//	}

//	public function beforeDelete($model, $cascade = true){
//
//	}

	public function afterDelete($model){

	}

	public function onError($model, $error){

	}
}
