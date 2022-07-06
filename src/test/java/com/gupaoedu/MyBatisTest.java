package com.gupaoedu;

import com.github.pagehelper.PageHelper;
import com.gupaoedu.domain.associate.AuthorAndBlog;
import com.gupaoedu.domain.Blog;
import com.gupaoedu.domain.associate.BlogAndAuthor;
import com.gupaoedu.domain.associate.BlogAndComment;
import com.gupaoedu.mapper.BlogMapper;
import com.gupaoedu.mapper.BlogMapperExt;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: qingshan
 * MyBatis Maven演示工程
 */
public class MyBatisTest {

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void prepare() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }




    /**
     * 使用 MyBatis API方式
     * @throws IOException
     */
    @Test
    public void testStatement() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Blog blog = (Blog) session.selectOne("com.gupaoedu.mapper.BlogMapper.selectBlogById", 1);
            System.out.println(blog);
        } finally {
            session.close();
        }
    }

    /**
     * 通过 SqlSession.getMapper(XXXMapper.class)  接口方式
     * @throws IOException
     */
    @Test
    public void testSelect() throws IOException {
        SqlSession session = sqlSessionFactory.openSession(); // ExecutorType.BATCH
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog=new Blog();
            blog.setAuthorId(1001);
            List<Blog> blogList = mapper.selectBlogListChoose(blog);
            System.out.println(blogList.toArray());
        } finally {
            session.close();
        }
    }

    @Test
    public void testSelectList() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog = new Blog();
            blog.setName("测试");
            List<Blog> list1 = new ArrayList<Blog>();
            list1 = mapper.selectBlogListIf(blog);
        } finally {
            session.close();
        }
    }

    /**
     * 动态SQL批量插入
     * @throws IOException
     */
    @Test
    public void testInsert() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog blog = new Blog();
            blog.setBid(16889);
            blog.setName("来吧");
            blog.setAuthorId(2222);
            System.out.println(mapper.insertBlog(blog));
            session.commit();
        } finally {
            session.close();
        }
    }

    /**
     * 动态SQL批量删除
     * @throws IOException
     */
    @Test
    public void testDelete() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            List<Blog> list = new ArrayList<Blog>();
            Blog blog1 = new Blog();
            blog1.setBid(666);
            list.add(blog1);
            Blog blog2 = new Blog();
            blog2.setBid(777);
            list.add(blog2);
            mapper.deleteByList(list);
        } finally {
            session.close();
        }
    }

    /**
     * 规则区别更新
     * @throws IOException
     */
    @Test
    public void testUpdate() throws IOException {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH);
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            for (int i = 0; i < 3; i++) {
                Blog newBlog = new Blog();
                newBlog.setBid(i);
                newBlog.setName("修改以后的名字"+i);
                mapper.updateByPrimaryKey(newBlog);
            }
            session.commit();
        }
        finally {
            session.close();
        }
    }

    /**
     * 动态SQL批量更新
     * @throws IOException
     */
    @Test
    public void testUpdateBlogList() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            List<Blog> list = new ArrayList<Blog>();
            Blog blog1 = new Blog();
            blog1.setBid(666);
            blog1.setName("newName666");
            blog1.setAuthorId(666666);
            list.add(blog1);
            Blog blog2 = new Blog();
            blog2.setBid(777);
            blog2.setName("newName777");
            blog2.setAuthorId(777777);
            list.add(blog2);
            mapper.updateBlogList(list);
            session.commit();
        } finally {
            session.close();
        }
    }

    /**
     * # 和 $ 的区别
     * @throws IOException
     */
    @Test
    public void testSelectByBean() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            Blog queryBean = new Blog();
            queryBean.setName("MyBatis源码分析");
            List<Blog> blog = mapper.selectBlogByBean(queryBean);
            System.out.println("查询结果："+blog);
        } finally {
            session.close();
        }
    }

    /**
     * 逻辑分页
     * @throws IOException
     */
    @Test
    public void testSelectByRowBounds() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            int start = 0; // offset
            int pageSize = 2; // limit
            RowBounds rb = new RowBounds(start, pageSize);
            List<Blog> list = mapper.selectBlogList(rb); // 使用逻辑分页
            for(Blog b :list){
                System.out.println(b);
            }
        } finally {
            session.close();
        }
    }

    /**
     * Mapper.xml的继承性
     * @throws IOException
     */
    @Test
    public void testMapperExt() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapperExt mapper = session.getMapper(BlogMapperExt.class);
            Blog blog = mapper.selectBlogByName("MySQL从入门到改行");
            System.out.println(blog);
            // 继承了父Mapper的方法
            Blog blog1 = mapper.selectBlogById(1);
            System.out.println(blog1);
        } finally {
            session.close();
        }
    }

    /**
     * 一对一，一篇文章对应一个作者
     * 嵌套结果，不存在N+1问题
     */
    @Test
    public void testSelectBlogWithAuthorResult() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        BlogMapper mapper = session.getMapper(BlogMapper.class);

        BlogAndAuthor blog = mapper.selectBlogWithAuthorResult(1);
        System.out.println("-----------:"+blog);
    }

    /**
     * 一对一，一篇文章对应一个作者
     * 嵌套查询，会有N+1的问题
     */
    @Test
    public void testSelectBlogWithAuthorQuery() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        BlogMapper mapper = session.getMapper(BlogMapper.class);

        BlogAndAuthor blog = mapper.selectBlogWithAuthorQuery(1);
        System.out.println("-----------:"+blog.getClass());
        // 如果开启了延迟加载(lazyLoadingEnabled=true)，会在使用的时候才发出SQL
        // equals,clone,hashCode,toString也会触发延迟加载
        System.out.println("-----------调用toString方法:"+blog);
        System.out.println("-----------getAuthor:"+blog.getAuthor().toString());
        // 如果 aggressiveLazyLoading = true ，也会触发加载，否则不会
        //System.out.println("-----------getName:"+blog.getName());
    }

    /**
     * 一对多关联查询：一篇文章对应多条评论
     * @throws IOException
     */
    @Test
    public void testSelectBlogWithComment() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            BlogAndComment blog = mapper.selectBlogWithCommentById(1);
            System.out.println(blog);
        } finally {
            session.close();
        }
    }

    /**
     * 多对多关联查询：作者的文章的评论
     * @throws IOException
     */
    @Test
    public void testSelectAuthorWithBlog() throws IOException {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            BlogMapper mapper = session.getMapper(BlogMapper.class);
            List<AuthorAndBlog> authors = mapper.selectAuthorWithBlog();
            for (AuthorAndBlog author : authors){
                System.out.println(author);
            }
        } finally {
            session.close();
        }
    }

}
