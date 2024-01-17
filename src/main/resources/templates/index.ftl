<#-- @ftlvariable name="videos" type="kotlin.collections.List<net.grandcentrix.backend.models.Video>" -->
<!DOCTYPE html>
<html lang="en">
<head>Video Shuffle</head>
<body>
    <h1 style="text-align: center; font-family: 'Arial', sans-serif; text-transform: uppercase">Videos Shuffle</h1>
    <section style="display: flex; justify-content: flex-start; margin: 0 auto; width: 100%; height: 100%; flex-direction: column;align-items: center;">
        <iframe width="75%" height="75%"
                src="https://www.youtube.com/embed/${video.id}">
        </iframe>

        <#list 1..videos as video>
            <ul>
                <li>${video.title}</li>
            </ul>
<#--            <h2>The video at index ${item?index} is ${item}</h2>-->
        </#list>

        <button>
            <a href="/" style="font-size: 25px; text-decoration: none">Shuffle</a>
        </button>
        <button>
            <a href="/" style="font-size: 25px; text-decoration: none">Add a video</a>
        </button>
        <#--<button><a href="/" style="font-size: 25px; text-decoration: none">Delete a video</a></button>-->
    </section>
</body>
</html>
