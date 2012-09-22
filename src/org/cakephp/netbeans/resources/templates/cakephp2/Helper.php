<?php
<#assign licenseFirst = "/* ">
<#assign licensePrefix = " * ">
<#assign licenseLast = " */">
<#include "../Licenses/license-${project.license}.txt">

/**
 * CakePHP ${name}
 * @author ${user}
 */
class ${name} extends AppHelper {
	public $helpers = array();

	public function __construct(View $View, $settings = array()) {
		parent::__construct($View, $settings);
	}

	public function beforeRender($viewFile) {

	}

	public function afterRender($viewFile) {

	}

	public function beforeLayout($viewLayout) {

	}

	public function afterLayout($viewLayout) {

	}
}
