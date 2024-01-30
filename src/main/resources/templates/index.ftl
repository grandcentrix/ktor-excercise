<#import "_layout.ftl" as layout />
<@layout.header>

        <section class="container-left">
            <iframe width="100%" height="500px"
                    src="https://www.youtube.com/embed/${randomId}">
            </iframe>

            <a class="shuffle" href="/shuffle">Shuffle</a>
        </section>

        <section class="container-right">
            <h2>Added videos: </h2>
            <ul class="videos-list">

                <#list videos as video>
                    <li>
                        <a href="https://www.youtube.com/watch?v=${video.id}">${video.title}</a>
                        <a class="button" href="${video.id}/update">Edit</a>
                        <a class="button" href="${video.id}/delete">Delete</a>
                    </li>
                </#list>
            </ul>

            <section class="new-video">
                <h2>${actionTitle}</h2>
                <form action="${buttonAction}" method="POST">
                    <p>
                        <label>
                            <#if actionTitle == "Add a new video:">
                                <input placeholder="Insert video link" type="text" name="link">
                            </#if>
                            <#if actionTitle == "Update video title:">
                                <p>${link}</p>
                            </#if>

                        </label>
                    </p>
                    <p>
                        <label>
                            <input placeholder="Insert video title" type="text" name="title">
                        </label>
                    </p>
                    <p>
                        <input class="button" type="submit">
                    </p>
                </form>
                <p>${status}</p>
            </section>
        </section>

</@layout.header>
