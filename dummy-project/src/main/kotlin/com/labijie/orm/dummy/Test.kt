/**
 * @author Anders Xiao
 * @date 2024-06-21
 */
package com.labijie.orm.dummy

import com.labijie.orm.dummy.pojo.Post
import com.labijie.orm.dummy.pojo.dsl.PostDSL.deleteByPrimaryKey
import com.labijie.orm.dummy.pojo.dsl.PostDSL.insert
import com.labijie.orm.dummy.pojo.dsl.PostDSL.replace
import com.labijie.orm.dummy.pojo.dsl.PostDSL.selectByPrimaryKey
import com.labijie.orm.dummy.pojo.dsl.PostDSL.selectMany
import com.labijie.orm.dummy.pojo.dsl.PostDSL.selectOne
import com.labijie.orm.dummy.pojo.dsl.PostDSL.update
import com.labijie.orm.dummy.pojo.dsl.PostDSL.updateByPrimaryKey
import com.labijie.orm.dummy.pojo.dsl.PostDSL.upsert
import org.jetbrains.exposed.sql.andWhere


class Test {
    fun curd() {
        //insert
        val post = Post().apply {
            this.id = 123
            this.title = "Test"
            this.description = "Just a test."
        }

        //select by primary key
        val selectedPost: Post? = PostTable.selectByPrimaryKey(123)

        //select one by a column
        val postItem: Post? = PostTable.selectOne {
            andWhere { PostTable.title eq  "Test" }
        }

        //select multiple by a column
        val postList: List<Post> = PostTable.selectMany {
            andWhere { PostTable.title eq  "Test" }
        }

        //select multiple by a column, only select title, description columns
        val postListSelective: List<Post> = PostTable.selectMany(PostTable.title, PostTable.description) {
            andWhere { PostTable.title like  "T%" }
        }

        PostTable.insert(post)

        //update all columns by primary key
        PostTable.updateByPrimaryKey(post)

        //update by primary key, and only update title column.
        PostTable.updateByPrimaryKey(post, PostTable.title)

        //update by a column, and only update description column.
        PostTable.update(post, selective =  arrayOf(PostTable.description), limit = 1) {
            PostTable.title.eq("Test")
        }

        //delete by primary key
        PostTable.deleteByPrimaryKey(123)


        //Update or insert
        PostTable.upsert(post)

        //Replace
        PostTable.replace(post)

    }
}