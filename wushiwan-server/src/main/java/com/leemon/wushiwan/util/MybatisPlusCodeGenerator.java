package com.leemon.wushiwan.util;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.leemon.wushiwan.exception.LogicException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jeecgframework.codegenerate.generate.impl.CodeGenerateOne;
import org.jeecgframework.codegenerate.generate.pojo.TableVo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @description: MybatisPlus代码生成器
 * @author: limeng
 * @create: 2019-03-16 21:14
 **/
@Slf4j
public class MybatisPlusCodeGenerator {

	private static String OSX_SERVER_PATH = "XXX";

	/**
	 * 代码生成器使用说明（单表模型）
	 * <p>
	 * [1]、全局配置文件
	 * src/main/resources/jeecg/jeecg_config.properties 	： 代码生成器基础配置文件（项目路径、根业务包路径）
	 * src/main/resources/jeecg/jeecg_database.properties 	：代码生成器数据库配置文件（独立的数据源配置）
	 * <p>
	 * [3]、页面使用说明：
	 * 1. 页面生成路径： src/main/java/{业务包根路径}/{子业务包}/vue/
	 * 2. 使用方法，手工复制到webstorm项目下面
	 * 3. 配置访问菜单
	 */

	public static void main(String[] args) throws IOException {
		//是否生成的是system的文件，比如用户管理、权限管理
		boolean isSystem = false;
		//数据库表名
		String tableName = "core_banner";
		//生成的vue页面，可用于查询的字段（java中的字段，驼峰命名）
		String[] queryFieldArray = {};
		//功能描述
		String des = "banner管理";

		generateAllByMybatisPlus(isSystem, tableName);
		generateControllerAndVue(tableName, queryFieldArray, des);
		moveVueFileToAdmin(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName));
		System.out.println("----------------------------所有文件生成完成----------------------------");
	}

	private static void moveVueFileToAdmin(String entityName) throws IOException {
		String os = System.getProperty("os.name");
		String projectPath = OSX_SERVER_PATH;
		if (os != null && os.contains("Windows")) {
			projectPath = "XXX";
		}
		String vueFromPath = projectPath + File.separator + "src/main/java/com/leemon/wushiwan" + File.separator + "vue";
		String vueToPath = projectPath + File.separator + ".." + File.separator + "wushiwan-admin" + File.separator + "src" + File.separator + "views" + File.separator + entityName;
		File dirFile = new File(vueToPath);
		if (!dirFile.exists()) {
			log.info("建立文件夹 = {}", vueToPath);
			dirFile.mkdirs();
		}
		if (new File(vueFromPath).exists()) {
			log.info("拷贝文件，from = {}, to = {}", vueFromPath, vueToPath);
			if (!new File(vueToPath + File.separator + "index.vue").exists()) {

				Files.copy(
						new File(vueFromPath + File.separator + "index.vue"),
						new File(vueToPath + File.separator + "index.vue"));
			}
			if (!new File(vueToPath + File.separator + "EditDialog.vue").exists()) {
				Files.copy(
						new File(vueFromPath + File.separator + "EditDialog.vue"),
						new File(vueToPath + File.separator + "EditDialog.vue"));
			}
		}


		String hostPath = projectPath + File.separator + ".." + File.separator + "wushiwan-admin" + File.separator + "src" + File.separator + "config" + File.separator + "host.js";
		String result = Files.asCharSource(new File(hostPath), Charsets.UTF_8).read();
		if (!result.contains(String.format("%s地址", entityName))) {
			log.info("修改host文件 = {}", hostPath);

			String s = String.format("\n// %s地址\n", entityName);
			String pathStr = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityName).replace("_", "-");
			s += String.format("export const %s_LIST_URL = HOST + \"/%s/list\"\n", entityName.toUpperCase(), pathStr);
			s += String.format("export const %s_DELETE_URL = HOST + \"/%s/delete\"\n", entityName.toUpperCase(), pathStr);
			s += String.format("export const %s_ADD_URL = HOST + \"/%s/add\"\n", entityName.toUpperCase(), pathStr);
			s += String.format("export const %s_EDIT_URL = HOST + \"/%s/edit\"\n", entityName.toUpperCase(), pathStr);
			String finalString = result + s;
			Files.write(finalString.getBytes(StandardCharsets.UTF_8), new File(hostPath));
		}
		log.info("删除文件夹 = {}", vueFromPath);
		FileUtils.deleteDirectory(new File(vueFromPath));
	}

	private static void generateControllerAndVue(String tableName, String[] queryFieldArray, String des) throws IOException {
		String os = System.getProperty("os.name");
		ResourceBundle config_bundle = ResourceBundle.getBundle("jeecg/jeecg_config");
		String projPath = config_bundle.getString("project_path");
		if (os != null && os.contains("Windows")) {
			if (projPath.contains("/")) {
				throw new LogicException("请修改jeecg_config.properties文件中的project_path");
			}
		} else {
			if (projPath.contains(":")) {
				throw new LogicException("请修改jeecg_config.properties文件中的project_path");
			}
		}
		String controllerPath = projPath + File.separator + "src/main/java/com/leemon/wushiwan/controller" + File.separator + CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName) + "Controller.java";
		File controllerFile = new File(controllerPath);
		if (controllerFile.exists()) {
			if (Files.readLines(controllerFile, StandardCharsets.UTF_8).size() > 30) {
				if (FileUtils.readFileToString(controllerFile).contains("PreAuthorize")) {//已经生成过
					return;
				}
			}
		}
		/**[2]、表配置参数 */
		TableVo table = new TableVo();
		//表名
		table.setTableName(tableName);
		//子业务包名
		table.setEntityPackage("wushiwan");
		//实体类名
		table.setEntityName(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName));
		table.setForeignKeys(queryFieldArray);
		table.setSequenceCode(tableName);
		//功能描述
		table.setFtlDescription(des);
		new CodeGenerateOne(table).generateCodeFile();
	}

	private static void generateAllByMybatisPlus(boolean isSystem, String tableName) {
		// 代码生成器
		AutoGenerator mpg = new AutoGenerator();

		// 全局配置
		GlobalConfig gc = new GlobalConfig();
		gc.setFileOverride(false);
		String os = System.getProperty("os.name");
		String projectPath = OSX_SERVER_PATH;
		if (os != null && os.contains("Windows")) {
			projectPath = "XXX";
		}

		gc.setOutputDir(projectPath + "/src/main/java");
		gc.setAuthor("XXX");
		gc.setOpen(false);
		mpg.setGlobalConfig(gc);

		// 数据源配置
		DataSourceConfig dsc = new DataSourceConfig();
		dsc.setUrl("jdbc:mysql://XXX:3306/XXX?useUnicode=true&characterEncoding=utf-8");
		// dsc.setSchemaName("public");
		dsc.setDriverName("com.mysql.cj.jdbc.Driver");
		dsc.setUsername("XXX");
		dsc.setPassword("XXX");
		mpg.setDataSource(dsc);

		// 包配置
		PackageConfig pc = new PackageConfig();
//		pc.setModuleName(scanner("模块名"));
//		pc.setParent("com.leemon.wushiwan");
		if (isSystem) {
			pc.setParent("com.leemon.wushiwan.system");
		} else {
			pc.setParent("com.leemon.wushiwan");
		}

		mpg.setPackageInfo(pc);

		// 自定义配置
		InjectionConfig cfg = new InjectionConfig() {
			@Override
			public void initMap() {
				// to do nothing
			}
		};

		// 如果模板引擎是 freemarker
		String templatePath = "/templates/mapper.xml.ftl";
		// 如果模板引擎是 velocity
		// String templatePath = "/templates/mapper.xml.vm";

		// 自定义输出配置
		List<FileOutConfig> focList = new ArrayList<>();
		final String path = projectPath;
		// 自定义配置会被优先输出
		focList.add(new FileOutConfig(templatePath) {
			@Override
			public String outputFile(com.baomidou.mybatisplus.generator.config.po.TableInfo tableInfo) {
				// 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
				return path + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
			}
		});

		cfg.setFileOutConfigList(focList);
		mpg.setCfg(cfg);

		// 配置模板
		TemplateConfig templateConfig = new TemplateConfig();

		// 配置自定义输出模板
		//指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
		// templateConfig.setEntity("templates/entity2.java");
		// templateConfig.setService();
		// templateConfig.setController();

		templateConfig.setXml(null);
		mpg.setTemplate(templateConfig);

		// 策略配置
		StrategyConfig strategy = new StrategyConfig();
		strategy.setSkipView(true);
		strategy.setNaming(NamingStrategy.underline_to_camel);
		strategy.setColumnNaming(NamingStrategy.underline_to_camel);
		strategy.setSuperEntityClass("com.leemon.wushiwan.entity.BaseEntity");
		strategy.setEntityLombokModel(true);
		strategy.setEntityTableFieldAnnotationEnable(true);
		strategy.setRestControllerStyle(true);
		strategy.setSuperControllerClass("com.leemon.wushiwan.controller.BaseController");
//		strategy.setInclude("core_agency", "core_img", "core_login_auth", "core_mission", "core_mission_accept", "core_mission_detail", "core_partner", "core_qrcode", "pay_bill", "pay_payment_mode", "social_follow", "social_notice", "sys_advice", "sys_property", "sys_user", "core_mission_rule", "sys_menu", "sys_role", "sys_role_menu", "sys_user_role");
		strategy.setInclude(tableName);
		strategy.setSuperEntityColumns("id", "creator", "create_time", "updater", "update_time", "deleted");
//		strategy.setLogicDeleteFieldName("deleted");
		strategy.setControllerMappingHyphenStyle(true);
//		strategy.setTablePrefix(pc.getModuleName() + "_");
		mpg.setStrategy(strategy);
		mpg.setTemplateEngine(new FreemarkerTemplateEngine());
		mpg.execute();
	}
}
